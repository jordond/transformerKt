package dev.transformerkt

import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.transformer.Composition
import androidx.media3.transformer.ExportException
import androidx.media3.transformer.ExportResult
import androidx.media3.transformer.ProgressHolder
import androidx.media3.transformer.TransformationRequest
import androidx.media3.transformer.Transformer
import dev.transformerkt.TransformerKt.Status
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File

internal class InternalTransformerKt(
    private val transformer: Transformer,
    private val progressPollDelayMs: Long,
) : TransformerKt {

    /**
     * @see TransformerKt.executeFlow
     */
    override fun executeFlow(
        input: TransformerKt.Input,
        output: File,
        request: TransformationRequest,
    ): Flow<Status> = callbackFlow {
        var isFinished = false
        val listener = object : Transformer.Listener {
            override fun onCompleted(composition: Composition, exportResult: ExportResult) {
                isFinished = true
                trySend(Status.Success(output))
                close()
            }

            override fun onError(
                composition: Composition,
                exportResult: ExportResult,
                exportException: ExportException,
            ) {
                isFinished = true
                trySend(Status.Failure(exportException))
                close()
            }
        }

        val transformer = buildTransformer(request, listener)
        transformer.start(input, output)

        val progressHolder = ProgressHolder()
        var previousProgress = 0
        val progressJob = launch {
            while (isActive) {
                if (isFinished) {
                    break
                }

                val progressState = transformer.getProgress(progressHolder)
                val progress = progressHolder.progress
                if (progress > previousProgress) {
                    previousProgress = progress
                    trySend(Status.Progress(progress))
                }

                if (progressState != Transformer.PROGRESS_STATE_NOT_STARTED) {
                    delay(progressPollDelayMs)
                }
            }
        }

        awaitClose {
            progressJob.cancel()
            if (!isFinished) {
                transformer.cancel()
            }
        }
    }.flowOn(Dispatchers.Main)

    /**
     * @see TransformerKt.execute
     */
    override suspend fun execute(
        input: TransformerKt.Input,
        output: File,
        request: TransformationRequest,
        onProgress: (Int) -> Unit,
    ): Status.Finished {
        try {
            var result: Status? = null
            executeFlow(input, output, request).collect { status ->
                result = status
                if (status is Status.Progress) {
                    onProgress(status.progress)
                }
            }

            if (result == null || result !is Status.Finished) {
                error("Unexpected result: $result")
            }

            return result as Status.Finished
        } catch (cause: Throwable) {
            if (cause is CancellationException) throw cause

            return Status.Failure(cause)
        }
    }

    private fun buildTransformer(
        request: TransformationRequest,
        listener: Transformer.Listener,
    ): Transformer {
        return transformer.buildUpon()
            .setTransformationRequest(request)
            .addListener(listener)
            .build()
    }

    private fun Transformer.start(input: TransformerKt.Input, output: File) {
        val outputPath = output.absolutePath
        when (input) {
            is TransformerKt.Input.MediaItem -> start(input.value, outputPath)
            is TransformerKt.Input.EditedMediaItem -> start(input.value, outputPath)
            is TransformerKt.Input.Uri -> start(MediaItem.fromUri(input.value), outputPath)
            is TransformerKt.Input.File -> {
                start(MediaItem.fromUri(input.value.toUri()), outputPath)
            }
        }
    }
}
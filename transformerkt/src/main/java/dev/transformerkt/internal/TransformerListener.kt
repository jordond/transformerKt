package dev.transformerkt.internal

import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.transformer.Composition
import androidx.media3.transformer.ExportException
import androidx.media3.transformer.ExportResult
import androidx.media3.transformer.ProgressHolder
import androidx.media3.transformer.TransformationRequest
import androidx.media3.transformer.Transformer
import dev.transformerkt.TransformerKt
import dev.transformerkt.ktx.buildWith
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File

/**
 * Converts a [Transformer] to a [Flow] that emits [TransformerKt.Status].
 *
 * All existing listeners on [Transformer] will be removed and replaced with a new listener that
 * converts the updates to a [Flow].
 *
 * **Note:** This must flow on [Dispatchers.Main] as [Transformer] expects a Looper.
 *
 * @receiver The [Transformer] instance to start a transformation.
 * @param[input] The input to transform.
 * @param[output] The output file to write to.
 * @param[request] The [TransformationRequest] to use.
 * @param[progressPollDelayMs] The delay between polling for progress.
 * @return A [Flow] that emits [TransformerKt.Status].
 */
internal fun Transformer.createTransformerCallbackFlow(
    input: TransformerKt.Input,
    output: File,
    request: TransformationRequest,
    progressPollDelayMs: Long = TransformerKt.DEFAULT_PROGRESS_POLL_DELAY_MS,
): Flow<TransformerKt.Status> {
    val oldTransformer = this
    return callbackFlow {
        var isFinished = false
        val listener = object : Transformer.Listener {
            override fun onCompleted(composition: Composition, exportResult: ExportResult) {
                isFinished = true
                trySend(TransformerKt.Status.Success(output))
                close()
            }

            override fun onError(
                composition: Composition,
                exportResult: ExportResult,
                exportException: ExportException,
            ) {
                isFinished = true
                trySend(TransformerKt.Status.Failure(exportException))
                close()
            }
        }

        val transformer = oldTransformer.buildWith {
            removeAllListeners()
            setTransformationRequest(request)
            addListener(listener)
        }
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
                    trySend(TransformerKt.Status.Progress(progress))
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
    }.catch { cause ->
        if (cause != CancellationException()) {
            emit(TransformerKt.Status.Failure(cause))
        }
    }.flowOn(Dispatchers.Main)
}

/**
 * Map an [TransformerKt.Input] into a value that [Transformer] can use.
 */
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
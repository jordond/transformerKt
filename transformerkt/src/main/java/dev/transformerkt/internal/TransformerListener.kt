package dev.transformerkt.internal

import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.transformer.Composition
import androidx.media3.transformer.ExportException
import androidx.media3.transformer.ExportResult
import androidx.media3.transformer.ProgressHolder
import androidx.media3.transformer.Transformer
import dev.transformerkt.TransformerKt
import dev.transformerkt.TransformerStatus
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
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Converts a [Transformer] to a [Flow] that emits [TransformerStatus].
 *
 * All existing listeners on [Transformer] will be removed and replaced with a new listener that
 * converts the updates to a [Flow].
 *
 * **Note:** This must flow on [Dispatchers.Main] as [Transformer] expects a Looper.
 *
 * @receiver The [Transformer] instance to start a transformation.
 * @param[input] The input to transform.
 * @param[output] The output file to write to.
 * @param[progressPollDelayMs] The delay between polling for progress.
 * @return A [Flow] that emits [TransformerStatus].
 */
internal fun Transformer.createTransformerCallbackFlow(
    input: TransformerInput,
    output: File,
    progressPollDelayMs: Long = TransformerKt.DEFAULT_PROGRESS_POLL_DELAY_MS,
): Flow<TransformerStatus> {
    val oldTransformer = this
    return callbackFlow {
        val listener = object : Transformer.Listener {
            override fun onCompleted(composition: Composition, exportResult: ExportResult) {
                trySend(TransformerStatus.Success(output, exportResult))
                close()
            }

            override fun onError(
                composition: Composition,
                exportResult: ExportResult,
                exportException: ExportException,
            ) {
                trySend(TransformerStatus.Failure(exportException))
                close()
            }
        }

        val transformer = oldTransformer
            .buildWith {
                removeAllListeners()
                addListener(listener)
            }
            .startWith(input, output)

        trySend(TransformerStatus.Progress(0))

        val progressHolder = ProgressHolder()
        var progressState = Transformer.PROGRESS_STATE_NOT_STARTED
        var previousProgress = 0
        val progressJob = launch(Dispatchers.Default) {
            while (isActive && progressState != Transformer.PROGRESS_STATE_UNAVAILABLE) {
                progressState = withContext(Dispatchers.Main) {
                    transformer.getProgress(progressHolder)
                }

                if (progressState == Transformer.PROGRESS_STATE_AVAILABLE) {
                    val progress = progressHolder.progress
                    if (progress > previousProgress) {
                        previousProgress = progress
                        trySend(TransformerStatus.Progress(progress))
                    }
                } else if (progressState == Transformer.PROGRESS_STATE_UNAVAILABLE) {
                    break
                }

                delay(progressPollDelayMs)
            }
        }

        awaitClose {
            progressJob.cancel()
            transformer.cancel()
        }
    }.catch { cause ->
        if (cause != CancellationException()) {
            emit(TransformerStatus.Failure(cause))
        }
    }.flowOn(Dispatchers.Main)
}

/**
 * Map an [TransformerInput] into a value that [Transformer] can use.
 */
private fun Transformer.startWith(input: TransformerInput, output: File): Transformer = apply {
    val outputPath = output.absolutePath
    when (input) {
        is TransformerInput.MediaItem -> start(input.value, outputPath)
        is TransformerInput.EditedMediaItem -> start(input.value, outputPath)
        is TransformerInput.Uri -> start(MediaItem.fromUri(input.value), outputPath)
        is TransformerInput.File -> {
            start(MediaItem.fromUri(input.value.toUri()), outputPath)
        }
        is TransformerInput.Composition -> start(input.value, outputPath)
    }
}
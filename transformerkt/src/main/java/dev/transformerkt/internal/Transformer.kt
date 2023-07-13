package dev.transformerkt.internal

import androidx.annotation.CheckResult
import androidx.media3.transformer.TransformationRequest
import androidx.media3.transformer.Transformer
import dev.transformerkt.TransformerKt
import dev.transformerkt.TransformerStatus
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.flow.onEach
import java.io.File

/**
 * Start a [Transformer] request and return a [Flow] of [TransformerStatus].
 *
 * @see createTransformerCallbackFlow
 * @param[input] The input to transform.
 * @param[output] The output file to write to.
 * @param[request] The [TransformationRequest] to use.
 * @param[progressPollDelayMs] The delay between polling for progress.
 * @return A [Flow] that emits [TransformerStatus].
 */
@CheckResult
internal fun Transformer.start(
    input: TransformerInput,
    output: File,
    request: TransformationRequest,
    progressPollDelayMs: Long = TransformerKt.DEFAULT_PROGRESS_POLL_DELAY_MS,
): Flow<TransformerStatus> = createTransformerCallbackFlow(
    input = input,
    output = output,
    request = request,
    progressPollDelayMs = progressPollDelayMs,
)

/**
 * Start a [Transformer] request in a coroutine and return a [TransformerStatus.Finished]
 * when the request is finished.
 *
 * For progress updates pass a [onProgress] callback.
 *
 * @see start
 * @param[input] The input to transform.
 * @param[output] The output file to write to.
 * @param[request] The [TransformationRequest] to use.
 * @param[progressPollDelayMs] The delay between polling for progress.
 * @param[onProgress] The callback to use for progress updates.
 * @return A [TransformerStatus.Finished] status.
 */
internal suspend fun Transformer.start(
    input: TransformerInput,
    output: File,
    request: TransformationRequest,
    progressPollDelayMs: Long = TransformerKt.DEFAULT_PROGRESS_POLL_DELAY_MS,
    onProgress: (Int) -> Unit = {},
): TransformerStatus.Finished {
    try {
        val result: TransformerStatus? = start(
            input = input,
            output = output,
            request = request,
            progressPollDelayMs = progressPollDelayMs,
        ).onEach { status ->
            if (status is TransformerStatus.Progress) {
                onProgress(status.progress)
            }
        }.lastOrNull()

        if (result == null || result !is TransformerStatus.Finished) {
            error("Unexpected finish result: $result")
        }

        return result
    } catch (cause: Throwable) {
        if (cause is CancellationException) throw cause

        return TransformerStatus.Failure(cause)
    }
}
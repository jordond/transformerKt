package dev.transformerkt.ktx

import androidx.annotation.CheckResult
import androidx.media3.transformer.TransformationRequest
import androidx.media3.transformer.Transformer
import dev.transformerkt.TransformerKt
import dev.transformerkt.internal.createTransformerCallbackFlow
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import java.io.File

/**
 * Start a [Transformer] request and return a [Flow] of [TransformerKt.Status].
 *
 * @see createTransformerCallbackFlow
 * @param[input] The input to transform.
 * @param[output] The output file to write to.
 * @param[request] The [TransformationRequest] to use.
 * @param[progressPollDelayMs] The delay between polling for progress.
 * @return A [Flow] that emits [TransformerKt.Status].
 */
@CheckResult
public fun Transformer.start(
    input: TransformerKt.Input,
    output: File,
    request: TransformationRequest,
    progressPollDelayMs: Long = TransformerKt.DEFAULT_PROGRESS_POLL_DELAY_MS,
): Flow<TransformerKt.Status> = createTransformerCallbackFlow(
    input = input,
    output = output,
    request = request,
    progressPollDelayMs = progressPollDelayMs,
)

/**
 * Start a [Transformer] request in a coroutine and return a [TransformerKt.Status.Finished]
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
 * @return A [TransformerKt.Status.Finished] status.
 */
public suspend fun Transformer.start(
    input: TransformerKt.Input,
    output: File,
    request: TransformationRequest,
    progressPollDelayMs: Long = TransformerKt.DEFAULT_PROGRESS_POLL_DELAY_MS,
    onProgress: (Int) -> Unit = {},
): TransformerKt.Status.Finished {
    try {
        var result: TransformerKt.Status? = null
        start(
            input = input,
            output = output,
            request = request,
            progressPollDelayMs = progressPollDelayMs,
        ).collect { status ->
            result = status
            if (status is TransformerKt.Status.Progress) {
                onProgress(status.progress)
            }
        }

        if (result == null || result !is TransformerKt.Status.Finished) {
            error("Unexpected finish result: $result")
        }

        return result as TransformerKt.Status.Finished
    } catch (cause: Throwable) {
        if (cause is CancellationException) throw cause

        return TransformerKt.Status.Failure(cause)
    }
}

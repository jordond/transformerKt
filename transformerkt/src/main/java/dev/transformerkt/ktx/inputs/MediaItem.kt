package dev.transformerkt.ktx.inputs

import androidx.annotation.CheckResult
import androidx.media3.common.MediaItem
import androidx.media3.transformer.TransformationRequest
import androidx.media3.transformer.Transformer
import dev.transformerkt.TransformerKt
import dev.transformerkt.TransformerStatus
import dev.transformerkt.internal.TransformerInput
import dev.transformerkt.internal.createTransformerCallbackFlow
import dev.transformerkt.internal.start
import kotlinx.coroutines.flow.Flow
import java.io.File

/**
 * Start a [Transformer] request for a [MediaItem] and return a [Flow] of [TransformerStatus].
 *
 * @see createTransformerCallbackFlow
 * @param[input] The input to transform.
 * @param[output] The output file to write to.
 * @param[request] The [TransformationRequest] to use.
 * @param[progressPollDelayMs] The delay between polling for progress.
 * @return A [Flow] that emits [TransformerStatus].
 */
@Deprecated(
    "Using TransformerRequest has been deprecated",
    ReplaceWith("start(input, output, progressPollDelayMs)"),
)
@CheckResult
public fun Transformer.start(
    input: MediaItem,
    output: File,
    request: TransformationRequest,
    progressPollDelayMs: Long = TransformerKt.DEFAULT_PROGRESS_POLL_DELAY_MS,
): Flow<TransformerStatus> = start(input, output, progressPollDelayMs)

/**
 * Start a [Transformer] request for a [MediaItem] and return a [Flow] of [TransformerStatus].
 *
 * @see createTransformerCallbackFlow
 * @param[input] The input to transform.
 * @param[output] The output file to write to.
 * @param[progressPollDelayMs] The delay between polling for progress.
 * @return A [Flow] that emits [TransformerStatus].
 */
@CheckResult
public fun Transformer.start(
    input: MediaItem,
    output: File,
    progressPollDelayMs: Long = TransformerKt.DEFAULT_PROGRESS_POLL_DELAY_MS,
): Flow<TransformerStatus> = start(
    input = TransformerInput.MediaItem(input),
    output = output,
    progressPollDelayMs = progressPollDelayMs,
)

/**
 * Start a [Transformer] request for a [MediaItem] in a coroutine and return
 * a [TransformerStatus.Finished] when the request is finished.
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
@Deprecated(
    "Using TransformerRequest has been deprecated",
    ReplaceWith("start(input, output, progressPollDelayMs)"),
)
public suspend fun Transformer.start(
    input: MediaItem,
    output: File,
    request: TransformationRequest,
    progressPollDelayMs: Long = TransformerKt.DEFAULT_PROGRESS_POLL_DELAY_MS,
    onProgress: (Int) -> Unit = {},
): TransformerStatus.Finished = start(input, output, progressPollDelayMs, onProgress)

/**
 * Start a [Transformer] request for a [MediaItem] in a coroutine and return
 * a [TransformerStatus.Finished] when the request is finished.
 *
 * For progress updates pass a [onProgress] callback.
 *
 * @see start
 * @param[input] The input to transform.
 * @param[output] The output file to write to.
 * @param[progressPollDelayMs] The delay between polling for progress.
 * @param[onProgress] The callback to use for progress updates.
 * @return A [TransformerStatus.Finished] status.
 */
public suspend fun Transformer.start(
    input: MediaItem,
    output: File,
    progressPollDelayMs: Long = TransformerKt.DEFAULT_PROGRESS_POLL_DELAY_MS,
    onProgress: (Int) -> Unit = {},
): TransformerStatus.Finished = start(
    input = TransformerInput.MediaItem(input),
    output = output,
    progressPollDelayMs = progressPollDelayMs,
    onProgress = onProgress,
)
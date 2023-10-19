package dev.transformerkt.ktx.inputs

import androidx.annotation.CheckResult
import androidx.media3.transformer.TransformationRequest
import androidx.media3.transformer.Transformer
import dev.transformerkt.TransformerKt
import dev.transformerkt.TransformerStatus
import dev.transformerkt.dsl.composition.compositionOf
import dev.transformerkt.dsl.effects.EffectsBuilder
import dev.transformerkt.internal.TransformerInput
import dev.transformerkt.internal.createTransformerCallbackFlow
import dev.transformerkt.internal.start
import kotlinx.coroutines.flow.Flow
import java.io.File

/**
 * Start a [Transformer] request for a [File] and return a [Flow] of [TransformerStatus].
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
    input: File,
    output: File,
    request: TransformationRequest,
    progressPollDelayMs: Long = TransformerKt.DEFAULT_PROGRESS_POLL_DELAY_MS,
): Flow<TransformerStatus> = start(input, output, progressPollDelayMs)

/**
 * Start a [Transformer] request for a [File] and return a [Flow] of [TransformerStatus].
 *
 * @see createTransformerCallbackFlow
 * @param[input] The input to transform.
 * @param[output] The output file to write to.
 * @param[progressPollDelayMs] The delay between polling for progress.
 * @return A [Flow] that emits [TransformerStatus].
 */
@CheckResult
public fun Transformer.start(
    input: File,
    output: File,
    progressPollDelayMs: Long = TransformerKt.DEFAULT_PROGRESS_POLL_DELAY_MS,
): Flow<TransformerStatus> = start(
    input = TransformerInput.File(input),
    output = output,
    progressPollDelayMs = progressPollDelayMs,
)

/**
 * Convert an image to a video.
 *
 * Use [effectsBlock] to customize the effects for the final video.
 *
 * @param[input] The input image to transform.
 * @param[output] The output file to write to.
 * @param[request] The [TransformationRequest] to use.
 * @param[durationMs] The duration of the final video.
 * @param[frameRate] The frame rate of the final video.
 * @param[progressPollDelayMs] The delay between polling for progress.
 * @param[effectsBlock] A block to customize the effects for the final video.
 * @return A [Flow] that emits [TransformerStatus].
 */
@Deprecated(
    "Using TransformerRequest has been deprecated",
    ReplaceWith("imageToVideo(input, output, durationMs, frameRate, progressPollDelayMs, effectsBlock)"),
)
@CheckResult
public fun Transformer.imageToVideo(
    input: File,
    output: File,
    request: TransformationRequest,
    durationMs: Long,
    frameRate: Int = 30,
    progressPollDelayMs: Long = TransformerKt.DEFAULT_PROGRESS_POLL_DELAY_MS,
    effectsBlock: EffectsBuilder.() -> Unit = {},
): Flow<TransformerStatus> =
    imageToVideo(input, output, durationMs, frameRate, progressPollDelayMs, effectsBlock)

/**
 * Convert an image to a video.
 *
 * Use [effectsBlock] to customize the effects for the final video.
 *
 * @param[input] The input image to transform.
 * @param[output] The output file to write to.
 * @param[durationMs] The duration of the final video.
 * @param[frameRate] The frame rate of the final video.
 * @param[progressPollDelayMs] The delay between polling for progress.
 * @param[effectsBlock] A block to customize the effects for the final video.
 * @return A [Flow] that emits [TransformerStatus].
 */
@CheckResult
public fun Transformer.imageToVideo(
    input: File,
    output: File,
    durationMs: Long,
    frameRate: Int = 30,
    progressPollDelayMs: Long = TransformerKt.DEFAULT_PROGRESS_POLL_DELAY_MS,
    effectsBlock: EffectsBuilder.() -> Unit = {},
): Flow<TransformerStatus> {
    val composition = compositionOf {
        sequenceOf {
            image(input, durationMs, frameRate, effectsBlock)
        }
    }

    return start(
        input = composition,
        output = output,
        progressPollDelayMs = progressPollDelayMs,
    )
}

/**
 * Start a [Transformer] request for a [File] in a coroutine and return
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
    ReplaceWith("start(input, output, progressPollDelayMs, onProgress)"),
)
public suspend fun Transformer.start(
    input: File,
    output: File,
    request: TransformationRequest,
    progressPollDelayMs: Long = TransformerKt.DEFAULT_PROGRESS_POLL_DELAY_MS,
    onProgress: (Int) -> Unit = {},
): TransformerStatus.Finished = start(input, output, progressPollDelayMs, onProgress)

/**
 * Start a [Transformer] request for a [File] in a coroutine and return
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
    input: File,
    output: File,
    progressPollDelayMs: Long = TransformerKt.DEFAULT_PROGRESS_POLL_DELAY_MS,
    onProgress: (Int) -> Unit = {},
): TransformerStatus.Finished = start(
    input = TransformerInput.File(input),
    output = output,
    progressPollDelayMs = progressPollDelayMs,
    onProgress = onProgress,
)

/**
 * Convert an image to a video in a coroutine.
 *
 * Use [effectsBlock] to customize the effects for the final video.
 *
 * @param[input] The input image to transform.
 * @param[output] The output file to write to.
 * @param[request] The [TransformationRequest] to use.
 * @param[durationMs] The duration of the final video.
 * @param[frameRate] The frame rate of the final video.
 * @param[progressPollDelayMs] The delay between polling for progress.
 * @param[effectsBlock] A block to customize the effects for the final video.
 * @param[onProgress] The callback to use for progress updates.
 * @return A [TransformerStatus.Finished] status.
 */
@Deprecated(
    "Using TransformerRequest has been deprecated",
    ReplaceWith("imageToVideo(input, output, durationMs, frameRate, progressPollDelayMs, effectsBlock, onProgress)"),
)
public suspend fun Transformer.imageToVideo(
    input: File,
    output: File,
    request: TransformationRequest,
    durationMs: Long,
    frameRate: Int = 30,
    progressPollDelayMs: Long = TransformerKt.DEFAULT_PROGRESS_POLL_DELAY_MS,
    effectsBlock: EffectsBuilder.() -> Unit = {},
    onProgress: (Int) -> Unit = {},
): TransformerStatus.Finished =
    imageToVideo(input, output, durationMs, frameRate, progressPollDelayMs, effectsBlock, onProgress)

/**
 * Convert an image to a video in a coroutine.
 *
 * Use [effectsBlock] to customize the effects for the final video.
 *
 * @param[input] The input image to transform.
 * @param[output] The output file to write to.
 * @param[durationMs] The duration of the final video.
 * @param[frameRate] The frame rate of the final video.
 * @param[progressPollDelayMs] The delay between polling for progress.
 * @param[effectsBlock] A block to customize the effects for the final video.
 * @param[onProgress] The callback to use for progress updates.
 * @return A [TransformerStatus.Finished] status.
 */
public suspend fun Transformer.imageToVideo(
    input: File,
    output: File,
    durationMs: Long,
    frameRate: Int = 30,
    progressPollDelayMs: Long = TransformerKt.DEFAULT_PROGRESS_POLL_DELAY_MS,
    effectsBlock: EffectsBuilder.() -> Unit = {},
    onProgress: (Int) -> Unit = {},
): TransformerStatus.Finished {
    val composition = compositionOf {
        sequenceOf {
            image(input, durationMs, frameRate, effectsBlock)
        }
    }

    return start(
        input = composition,
        output = output,
        progressPollDelayMs = progressPollDelayMs,
        onProgress = onProgress,
    )
}
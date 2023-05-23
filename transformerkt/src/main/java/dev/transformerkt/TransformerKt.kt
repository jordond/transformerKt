package dev.transformerkt

import android.content.Context
import androidx.media3.common.MimeTypes
import androidx.media3.transformer.TransformationRequest
import androidx.media3.transformer.Transformer
import dev.transformerkt.internal.InternalTransformerKt
import dev.transformerkt.ktx.buildWith
import kotlinx.coroutines.flow.Flow
import java.io.File

/**
 * A coroutine based wrapper around the [Transformer] API.
 *
 * Example using [TransformerKt]:
 *
 * ```
 * suspend fun transform(context: Context, input: Uri, output: File) {
 *    val transformer = TransformerKt.create(context)
 *    val result = transformer.execute(TransformerInput.from(input), output) { progress ->
 *        // Update UI with progress
 *    }
 *
 *    when (result) {
 *      is TransformerStatus.Success -> {
 *          // Handle success
 *      }
 *      is TransformerStatus.Failure -> {
 *          // Handle failure
 *      }
 * }
 * ```
 *
 * Or you can use the extension functions on a [Transformer] instance:
 *
 * ```
 * suspend fun transform(context: Context, input: Uri, output: File) {
 *     val transformer = Transformer.Builder(context).build()
 *     val result = transformer.start(input.asTransformerInput(), output) { progress ->
 *         // Update UI with progress
 *     }
 *
 *     // Handle the result
 * }
 * ```
 *
 * Or as a [Flow]:
 *
 * ```
 * suspend fun transform(context: Context, input: Uri, output: File) {
 *     val transformer = Transformer.Builder(context).build()
 *     transformer.start(input.asTransformerInput(), output).collect (status) {
 *          // Handle the result
 *     }
 * }
 * ```
 */
public interface TransformerKt {

    /**
     * Execute a [TransformationRequest] on the given [input] and write the result to [output]
     * and receive a [Flow] of [TransformerStatus].
     *
     * Example:
     *
     * ```
     * suspend fun transform(context: Context, input: Uri, output: File) {
     *   val transformer = TransformerKt.create(context)
     *   transformer.executeFlow(input, output).collect { status ->
     *       when (status) {
     *          is TransformerStatus.Success -> // Handle Success
     *          is TransformerStatus.Failure -> // Handle Failure
     *          is TransformerStatus.Progress -> // Update UI with progress value
     *       }
     *    }
     * }
     * ```
     *
     * @param[input] The [TransformerInput] to use as the input for the [Transformer].
     * @param[output] The [File] to write the result to.
     * @param[request] The [TransformationRequest] to use for the [Transformer].
     * @return A [Flow] of [TransformerStatus] for the [Transformer] execution.
     */
    public fun start(
        input: TransformerInput,
        output: File,
        request: TransformationRequest = DefaultRequest,
    ): Flow<TransformerStatus>

    /**
     * Execute a [TransformationRequest] on the given [input] and write the result to the [output].
     *
     * Example:
     *
     * ```
     * suspend fun transform(context: Context, input: Uri, output: File) {
     *    val transformer = TransformerKt.create(context)
     *    val request = TransformerKt.DefaultRequest.buildWith {
     *         setAudioMimeType(MimeTypes.AUDIO_AAC)
     *    }
     *
     *    val result = transformer.execute(input, output, request) { progress ->
     *        // Update UI with progress
     *    }
     *
     *    // Handle result
     * }
     * ```
     *
     * @param[input] The [TransformerInput] to use as the input for the [Transformer].
     * @param[output] The [File] to write the result to.
     * @param[request] The [TransformationRequest] to use for the [Transformer].
     * @param[onProgress] Callback to receive progress updates.
     * @return The [TransformerStatus.Finished] state of the [Transformer] execution.
     */
    public suspend fun start(
        input: TransformerInput,
        output: File,
        request: TransformationRequest = DefaultRequest,
        onProgress: (Int) -> Unit,
    ): TransformerStatus.Finished

    public companion object {

        /**
         * A [TransformationRequest] that uses the default values.
         */
        public val InferRequest: TransformationRequest = TransformationRequest.Builder().build()

        /**
         * A [TransformationRequest] that transforms the video to H264.
         */
        public val H264Request: TransformationRequest = TransformationRequest.Builder()
            .setVideoMimeType(MimeTypes.VIDEO_H264)
            .build()

        /**
         * A [TransformationRequest] that transforms the video to H264 and audio to AAC.
         */
        public val H264AndAacRequest: TransformationRequest = H264Request.buildWith {
            setAudioMimeType(MimeTypes.AUDIO_AAC)
        }

        public val DefaultRequest: TransformationRequest = InferRequest

        /**
         * The default delay between progress updates in milliseconds.
         */
        internal const val DEFAULT_PROGRESS_POLL_DELAY_MS = 500L

        /**
         * Create a [TransformerKt] that creates a default [Transformer] using the [context].
         *
         * @param[context] The [Context] to use for creating the [Transformer.Builder].
         * @param[progressPollDelayMs] The delay between progress updates in milliseconds.
         */
        public fun create(
            context: Context,
            progressPollDelayMs: Long = DEFAULT_PROGRESS_POLL_DELAY_MS,
        ): TransformerKt {
            val transformer = Transformer.Builder(context).build()
            return create(transformer, progressPollDelayMs)
        }

        /**
         * Create a [TransformerKt] that uses the provided [Transformer].
         *
         * @param[transformer] The [Transformer] to use for the [TransformerKt].
         * @param[progressPollDelayMs] The delay between progress updates in milliseconds.
         */
        public fun create(
            transformer: Transformer,
            progressPollDelayMs: Long = DEFAULT_PROGRESS_POLL_DELAY_MS,
        ): TransformerKt = InternalTransformerKt(transformer, progressPollDelayMs)
    }
}
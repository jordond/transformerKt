package dev.transformerkt

import android.content.Context
import androidx.annotation.IntRange
import androidx.media3.common.MimeTypes
import androidx.media3.transformer.TransformationRequest
import androidx.media3.transformer.Transformer
import dev.transformerkt.internal.InternalTransformerKt
import kotlinx.coroutines.flow.Flow
import java.io.File

/**
 * A coroutine based wrapper around the [Transformer] API.
 *
 * Example:
 *
 * ```
 * suspend fun transform(context: Context, input: Uri, output: File) {
 *    val transformer = TransformerExecutor.create(context)
 *    val result = transformer.execute(input, output) { progress ->
 *        // Update UI with progress
 *    }
 *
 *    when (result) {
 *      is Status.Success -> {
 *          // Handle success
 *      }
 *      is Status.Failure -> {
 *          // Handle failure
 *      }
 * }
 * ```
 */
public interface TransformerKt {

    /**
     * Execute a [TransformationRequest] on the given [input] and write the result to [output]
     * and receive a [Flow] of [Status].
     *
     * Example:
     *
     * ```
     * suspend fun transform(context: Context, input: Uri, output: File) {
     *   val transformer = TransformerExecutor.create(context)
     *   transformer.executeFlow(input, output).collect { status ->
     *       when (status) {
     *          is Status.Success -> // Handle Success
     *          is Status.Failure -> // Handle Failure
     *          is Status.Progress -> // Update UI with progress value
     *       }
     *    }
     * }
     * ```
     *
     * @param[input] The [Input] to use as the input for the [Transformer].
     * @param[output] The [File] to write the result to.
     * @param[request] The [TransformationRequest] to use for the [Transformer].
     * @return A [Flow] of [Status] for the [Transformer] execution.
     */
    public fun executeFlow(
        input: Input,
        output: File,
        request: TransformationRequest = DefaultRequest,
    ): Flow<Status>

    /**
     * Execute a [TransformationRequest] on the given [input] and write the result to the [output].
     *
     * Example:
     *
     * ```
     * suspend fun transform(context: Context, input: Uri, output: File) {
     *    val transformer = TransformerExecutor.create(context)
     *    val request = TransformerExecutor.DefaultRequest.buildWith {
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
     * @param[input] The [Input] to use as the input for the [Transformer].
     * @param[output] The [File] to write the result to.
     * @param[request] The [TransformationRequest] to use for the [Transformer].
     * @param[onProgress] Callback to receive progress updates.
     * @return The [Status.Finished] state of the [Transformer] execution.
     */
    public suspend fun execute(
        input: Input,
        output: File,
        request: TransformationRequest = DefaultRequest,
        onProgress: (Int) -> Unit,
    ): Status.Finished

    /**
     * A wrapper for all available inputs for [Transformer].
     */
    public sealed interface Input {

        /**
         * Will be passed to [Transformer] via [androidx.media3.common.MediaItem.fromUri].
         */
        @JvmInline
        public value class Uri(public val value: android.net.Uri) : Input

        /**
         * An input [File] that will be passed to [Transformer] via [androidx.media3.common.MediaItem.fromUri].
         *
         * **Note:** Make sure the [java.io.File] is accessible! Which means it should be located in
         * the app's internal storage.
         */
        @JvmInline
        public value class File(public val value: java.io.File) : Input

        @JvmInline
        public value class MediaItem(public val value: androidx.media3.common.MediaItem) : Input

        @JvmInline
        public value class EditedMediaItem(
            public val value: androidx.media3.transformer.EditedMediaItem,
        ) : Input

        public companion object {

            public fun from(uri: android.net.Uri): Uri = Uri(uri)
            public fun from(file: java.io.File): File = File(file)
            public fun from(item: androidx.media3.common.MediaItem): MediaItem = MediaItem(item)
            public fun from(
                item: androidx.media3.transformer.EditedMediaItem,
            ): EditedMediaItem = EditedMediaItem(item)
        }
    }

    /**
     * Defines the possible states of a [TransformationRequest] execution.
     */
    public sealed interface Status {

        /**
         * Denotes the completion of a [Transformer] execution.
         */
        public sealed interface Finished : Status

        /**
         * Current progress of a [Transformer] execution.
         *
         * @param[progress] Integer progress value between 0-100
         */
        public data class Progress(@IntRange(from = 0, to = 100) val progress: Int) : Status

        /**
         * A successful [Transformer] execution.
         *
         * @param[output] The output [File] of the [Transformer] execution.
         */
        public data class Success(val output: File) : Status, Finished

        /**
         * [Transformer] encountered a failure.
         *
         * @param[cause] The [Throwable] that caused the failure.
         */
        public data class Failure(val cause: Throwable) : Status, Finished
    }

    public companion object {

        /**
         * Create a [TransformationRequest] that uses the default values.
         */
        public val InferRequest: TransformationRequest = TransformationRequest.Builder().build()

        /**
         * Create a [TransformationRequest] that transformes the video to H264.
         */
        public val H264Request: TransformationRequest = TransformationRequest.Builder()
            .setVideoMimeType(MimeTypes.VIDEO_H264)
            .build()

        public val DefaultRequest: TransformationRequest = H264Request

        /**
         * The default delay between progress updates in milliseconds.
         */
        private const val DEFAULT_PROGRESS_POLL_DELAY_MS = 500L

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
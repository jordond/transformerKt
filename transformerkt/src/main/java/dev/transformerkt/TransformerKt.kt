package dev.transformerkt

import android.content.Context
import androidx.media3.common.MimeTypes
import androidx.media3.transformer.TransformationRequest
import androidx.media3.transformer.Transformer
import dev.transformerkt.ktx.buildWith
import kotlinx.coroutines.flow.Flow

/**
 * A Kotlin coroutine wrapper around Media3's [Transformer] API.
 *
 * Use the extension functions on a [Transformer] instance:
 *
 * ```
 * suspend fun transform(context: Context, input: Uri, output: File) {
 *     val transformer = Transformer.Builder(context).build()
 *     val result = transformer.start(input, output) { progress ->
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
 *     transformer.start(input, output).collect (status) {
 *          // Handle the result
 *     }
 * }
 * ```
 */
@Suppress("MemberVisibilityCanBePrivate")
public object TransformerKt {

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
     * Build a [Transformer] instance.
     *
     * @param[context] The [Context] to use to create the [Transformer.Builder].
     * @param[block] The block to use to configure the [Transformer.Builder].
     * @return The [Transformer] created from the [block].
     */
    public fun build(
        context: Context,
        block: Transformer.Builder.() -> Unit = {},
    ): Transformer = Transformer.Builder(context).buildWith(block)
}

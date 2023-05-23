package dev.transformerkt.internal

import androidx.media3.transformer.Transformer

/**
 * A wrapper for all available inputs for [Transformer].
 */
internal sealed interface TransformerInput {

    /**
     * Will be passed to [Transformer] via [androidx.media3.common.MediaItem.fromUri].
     */
    @JvmInline
    value class Uri(val value: android.net.Uri) : TransformerInput

    /**
     * An input [File] that will be passed to [Transformer] via [androidx.media3.common.MediaItem.fromUri].
     *
     * **Note:** Make sure the [java.io.File] is accessible! Which means it should be located in
     * the app's internal storage.
     */
    @JvmInline
    value class File(val value: java.io.File) : TransformerInput

    @JvmInline
    value class MediaItem(val value: androidx.media3.common.MediaItem) : TransformerInput

    @JvmInline
    value class EditedMediaItem(
        val value: androidx.media3.transformer.EditedMediaItem,
    ) : TransformerInput
}
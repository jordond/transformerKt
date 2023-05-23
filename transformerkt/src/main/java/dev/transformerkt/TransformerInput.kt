package dev.transformerkt

import androidx.media3.transformer.Transformer

/**
 * A wrapper for all available inputs for [Transformer].
 */
public sealed interface TransformerInput {

    /**
     * Will be passed to [Transformer] via [androidx.media3.common.MediaItem.fromUri].
     */
    @JvmInline
    public value class Uri(public val value: android.net.Uri) : TransformerInput

    /**
     * An input [File] that will be passed to [Transformer] via [androidx.media3.common.MediaItem.fromUri].
     *
     * **Note:** Make sure the [java.io.File] is accessible! Which means it should be located in
     * the app's internal storage.
     */
    @JvmInline
    public value class File(public val value: java.io.File) : TransformerInput

    @JvmInline
    public value class MediaItem(public val value: androidx.media3.common.MediaItem) : TransformerInput

    @JvmInline
    public value class EditedMediaItem(
        public val value: androidx.media3.transformer.EditedMediaItem,
    ) : TransformerInput

    public companion object {

        public fun of(uri: android.net.Uri): Uri = Uri(uri)
        public fun of(file: java.io.File): File = File(file)
        public fun of(item: androidx.media3.common.MediaItem): MediaItem = MediaItem(item)
        public fun of(
            item: androidx.media3.transformer.EditedMediaItem,
        ): EditedMediaItem = EditedMediaItem(item)
    }
}
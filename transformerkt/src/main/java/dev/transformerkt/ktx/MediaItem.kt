package dev.transformerkt.ktx

import androidx.media3.common.MediaItem
import androidx.media3.transformer.EditedMediaItem

/**
 * Build upon this [MediaItem] to create a new [MediaItem].
 *
 * @param[block] The block to use to configure the [MediaItem.Builder].
 * @return The [MediaItem] created from this [MediaItem.Builder].
 */
public fun MediaItem.buildWith(
    block: MediaItem.Builder.() -> Unit,
): MediaItem = buildUpon().apply(block).build()

/**
 * Convenience function for creating a [MediaItem.ClippingConfiguration].
 *
 * @param[startMs] The start position in milliseconds.
 * @param[endMs] The end position in milliseconds.
 */
public fun MediaItem.Builder.setClippingConfiguration(
    startMs: Long,
    endMs: Long,
): MediaItem.Builder = setClippingConfiguration(
    MediaItem.ClippingConfiguration.Builder()
        .setStartPositionMs(startMs)
        .setEndPositionMs(endMs)
        .build(),
)

/**
 * Create a [EditedMediaItem] from this [MediaItem.Builder].
 *
 * @receiver The [MediaItem.Builder] to use to create the [EditedMediaItem].
 * @param[block] The block to use to configure the [EditedMediaItem].
 * @return The [EditedMediaItem] created from this [EditedMediaItem.Builder].
 */
public fun MediaItem.Builder.edited(
    block: EditedMediaItem.Builder.() -> Unit,
): EditedMediaItem = EditedMediaItem.Builder(build()).apply(block).build()

/**
 * Create a [EditedMediaItem] from this [MediaItem].
 *
 * @receiver The [MediaItem.Builder] to use to create the [EditedMediaItem].
 * @param[block] The block to use to configure the [EditedMediaItem].
 * @return The [EditedMediaItem] created from this [EditedMediaItem.Builder].
 */
public fun MediaItem.edited(
    block: EditedMediaItem.Builder.() -> Unit,
): EditedMediaItem = buildUpon().edited(block)
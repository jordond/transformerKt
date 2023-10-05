package dev.transformerkt.ktx

import androidx.media3.common.MediaItem
import androidx.media3.transformer.EditedMediaItem
import androidx.media3.transformer.EditedMediaItemSequence

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

public fun MediaItem.asEdited(
    block: EditedMediaItem.Builder.() -> Unit = {},
): EditedMediaItem = edited(block)

public fun EditedMediaItem.toSequence(isLooping: Boolean = false): EditedMediaItemSequence =
    EditedMediaItemSequence(listOf(this), isLooping)

public fun List<EditedMediaItem>.toSequence(isLooping: Boolean = false): EditedMediaItemSequence =
    EditedMediaItemSequence(this, isLooping)

public operator fun EditedMediaItem.plus(other: EditedMediaItem): EditedMediaItemSequence =
    EditedMediaItemSequence(listOf(this, other), false)

public operator fun EditedMediaItemSequence.plus(other: EditedMediaItem): EditedMediaItemSequence =
    EditedMediaItemSequence(editedMediaItems.toList() + other, false)
package dev.transformerkt.ktx

import androidx.media3.common.MediaItem
import androidx.media3.transformer.EditedMediaItem
import androidx.media3.transformer.TransformationRequest
import androidx.media3.transformer.Transformer

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

/**
 * Build upon an existing [Transformer] instance.
 *
 * @param[block] The block to use to configure the [Transformer.Builder].
 * @return The [Transformer] created from this [Transformer.Builder].
 */
public fun Transformer.buildWith(
    block: Transformer.Builder.() -> Unit,
): Transformer = buildUpon().apply(block).build()

/**
 * Build upon an existing [TransformationRequest] instance.
 *
 * @param[block] The block to use to configure the [TransformationRequest.Builder].
 * @return The [TransformationRequest] created from this [TransformationRequest.Builder].
 */
public fun TransformationRequest.buildWith(
    block: TransformationRequest.Builder.() -> Unit,
): TransformationRequest = buildUpon().apply(block).build()
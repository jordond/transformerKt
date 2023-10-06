package dev.transformerkt.ktx.effects

import androidx.media3.effect.Presentation
import dev.transformerkt.dsl.effects.EffectsBuilder

/**
 * Creates a new [Presentation] instance.
 *
 * The output frame will have the given width and height, given a Presentation.Layout. Width and
 * height must be positive integers representing the output frame's width and height.
 *
 * @param[width] The width of the output frame, in pixels.
 * @param[height] The height of the output frame, in pixels.
 * @param[scale] The [LayoutScale] to use when resizing the input frame to the output frame.
 */
public fun resolutionEffect(
    width: Int,
    height: Int,
    scale: LayoutScale,
): Presentation = Presentation.createForWidthAndHeight(width, height, scale.layout)

/**
 * Creates a new [Presentation] instance.
 *
 * The output frame will have the given height. Width will scale to preserve the input aspect ratio.
 * For example, a 1920x1440 video can be scaled to 640x480 by passing a height of 480.
 *
 * @param[height] The height of the output frame, in pixels
 */
public fun resolutionEffect(
    height: Int,
): Presentation = Presentation.createForHeight(height)

/**
 * Creates a new [Presentation] instance.
 *
 * The output frame will have the given aspect ratio (width/height ratio). Width or height will
 * be resized to conform to this [aspectRatio], given a [scale].
 *
 * @param[aspectRatio] The aspect ratio (width/height ratio) of the output frame. Must be positive.
 * @param[scale] The [LayoutScale] to use when resizing the input frame to the output frame.
 */
public fun resolutionEffect(
    aspectRatio: Float,
    scale: LayoutScale,
): Presentation = Presentation.createForAspectRatio(aspectRatio, scale.layout)

/**
 * Adds a new [Presentation] effect for setting the width and height of the output.
 *
 * The output frame will have the given width and height, given a Presentation.Layout. Width and
 * height must be positive integers representing the output frame's width and height.
 *
 * @param[width] The width of the output frame, in pixels.
 * @param[height] The height of the output frame, in pixels.
 * @param[scale] The [LayoutScale] to use when resizing the input frame to the output frame.
 */
public fun EffectsBuilder.resolution(
    width: Int,
    height: Int,
    scale: LayoutScale,
): EffectsBuilder = apply {
    video(resolutionEffect(height, width, scale))
}

/**
 * Adds a new [Presentation] effect for setting the height of the output.
 *
 * The output frame will have the given height. Width will scale to preserve the input aspect ratio.
 * For example, a 1920x1440 video can be scaled to 640x480 by passing a height of 480.
 *
 * @param[height] The height of the output frame, in pixels
 */
public fun EffectsBuilder.resolution(
    height: Int,
): EffectsBuilder = apply {
    video(resolutionEffect(height))
}

/**
 * Adds a new [Presentation] effect for setting the aspect ratio of the output.
 *
 * The output frame will have the given aspect ratio (width/height ratio). Width or height will
 * be resized to conform to this [aspectRatio], given a [scale].
 *
 * @param[aspectRatio] The aspect ratio (width/height ratio) of the output frame. Must be positive.
 * @param[scale] The [LayoutScale] to use when resizing the input frame to the output frame.
 */
public fun EffectsBuilder.resolution(
    aspectRatio: Float,
    scale: LayoutScale,
): EffectsBuilder = apply {
    video(resolutionEffect(aspectRatio, scale))
}

/**
 * Type safe wrapper around [Presentation.Layout].
 */
public sealed class LayoutScale(internal val layout: Int) {

    /**
     * Empty pixels added above and below the input frame (for letterboxing), or to the left and
     * right of the input frame (for pillarboxing), until the desired aspect ratio is achieved.
     * All input frame pixels will be within the output frame.
     *
     * When applying:
     *
     * - letterboxing, the output width will default to the input width, and the output height
     *   will be scaled appropriately.
     * - pillarboxing, the output height will default to the input height, and the output width
     *   will be scaled appropriately.
     */
    public data object Fit : LayoutScale(Presentation.LAYOUT_SCALE_TO_FIT)

    /**
     * Pixels cropped from the input frame, until the desired aspect ratio is achieved. Pixels may
     * be cropped either from the bottom and top, or from the left and right sides, of
     * the input frame.
     *
     * When cropping from the:
     *
     *  - bottom and top, the output width will default to the input width, and the output height
     *    will be scaled appropriately.
     *  - left and right, the output height will default to the input height, and the output width
     *    will be scaled appropriately.
     */
    public data object Fill : LayoutScale(Presentation.LAYOUT_SCALE_TO_FIT_WITH_CROP)

    /**
     * Frame stretched larger on the x or y axes to fit the desired aspect ratio.
     *
     * When stretching to a:
     *
     * - taller aspect ratio, the output width will default to the input width, and the
     *   output height will be scaled appropriately.
     * - narrower aspect ratio, the output height will default to the input height, and the
     *   output width will be scaled appropriately.
     */
    public data object Stretch : LayoutScale(Presentation.LAYOUT_STRETCH_TO_FIT)
}
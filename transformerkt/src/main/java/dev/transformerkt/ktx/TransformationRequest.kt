package dev.transformerkt.ktx

import androidx.media3.transformer.TransformationRequest

/**
 * Build upon an existing [TransformationRequest] instance.
 *
 * @param[block] The block to use to configure the [TransformationRequest.Builder].
 * @return The [TransformationRequest] created from this [TransformationRequest.Builder].
 */
public fun TransformationRequest.buildWith(
    block: TransformationRequest.Builder.() -> Unit,
): TransformationRequest = buildUpon().apply(block).build()
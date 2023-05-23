package dev.transformerkt.ktx

import androidx.media3.transformer.Transformer

/**
 * Build upon an existing [Transformer.Builder] instance.
 *
 * @param[block] The block to use to configure the [Transformer.Builder].
 * @return The [Transformer] created from this [Transformer.Builder].
 */
public fun Transformer.Builder.buildWith(
    block: Transformer.Builder.() -> Unit,
): Transformer = apply(block).build()

/**
 * Build upon an existing [Transformer] instance.
 *
 * @param[block] The block to use to configure the [Transformer.Builder].
 * @return The [Transformer] created from this [Transformer.Builder].
 */
public fun Transformer.buildWith(
    block: Transformer.Builder.() -> Unit,
): Transformer = buildUpon().apply(block).build()
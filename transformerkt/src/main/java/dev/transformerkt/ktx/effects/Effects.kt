package dev.transformerkt.ktx.effects

import androidx.media3.common.MediaItem
import androidx.media3.transformer.Composition
import androidx.media3.transformer.EditedMediaItem
import androidx.media3.transformer.Effects
import dev.transformerkt.ktx.edited

public fun effects(
    block: EffectsBuilder.() -> Unit,
): Effects = DefaultEffectsBuilder().apply(block).build()

public fun EditedMediaItem.Builder.setEffects(
    block: EffectsBuilder.() -> Unit,
): EditedMediaItem.Builder = apply {
    setEffects(effects(block))
}

public fun MediaItem.withEffects(
    block: EffectsBuilder.() -> Unit,
): EditedMediaItem = edited {
    setEffects(block)
}

public fun Composition.Builder.setEffects(
    block: EffectsBuilder.() -> Unit,
): Composition.Builder = apply {
    setEffects(effects(block))
}

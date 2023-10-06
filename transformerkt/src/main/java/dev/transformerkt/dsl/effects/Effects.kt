package dev.transformerkt.dsl.effects

import androidx.media3.common.MediaItem
import androidx.media3.transformer.Composition
import androidx.media3.transformer.EditedMediaItem
import androidx.media3.transformer.Effects
import dev.transformerkt.ktx.edited

public fun buildEffects(
    block: EffectsBuilder.() -> Unit,
): Effects = DefaultEffectsBuilder().apply(block).build()

public fun EditedMediaItem.Builder.setEffects(
    block: EffectsBuilder.() -> Unit,
): EditedMediaItem.Builder = apply {
    val oldEffects = build().effects
    val newEffects = buildEffects(block)
    val merged = Effects(
        oldEffects.audioProcessors + newEffects.audioProcessors,
        oldEffects.videoEffects + newEffects.videoEffects,
    )

    setEffects(merged)
}

public fun EditedMediaItem.Builder.effects(
    block: EffectsBuilder.() -> Unit,
): EditedMediaItem.Builder = setEffects(block)

public fun MediaItem.withEffects(
    block: EffectsBuilder.() -> Unit,
): EditedMediaItem = edited {
    setEffects(block)
}

public fun Composition.Builder.setEffects(
    block: EffectsBuilder.() -> Unit,
): Composition.Builder = apply {
    setEffects(buildEffects(block))
}

package dev.transformerkt.dsl.effects

import androidx.media3.common.Effect
import androidx.media3.common.audio.AudioProcessor
import androidx.media3.transformer.Effects
import dev.transformerkt.dsl.composition.CompositionDsl

@DslMarker
internal annotation class EffectsDsl

@EffectsDsl
public interface EffectsBuilder {

    public fun audio(processor: AudioProcessor): EffectsBuilder

    public fun audio(processors: List<AudioProcessor>): EffectsBuilder

    public fun audio(vararg processors: AudioProcessor): EffectsBuilder = apply {
        audio(processors.toList())
    }

    public fun video(effect: Effect): EffectsBuilder

    public fun video(effects: List<Effect>): EffectsBuilder

    public fun video(vararg effects: Effect): EffectsBuilder = apply {
        video(effects.toList())
    }

    public fun build(): Effects
}

internal class DefaultEffectsBuilder : EffectsBuilder {

    private val audioEffects = mutableListOf<AudioProcessor>()
    private val videoEffects = mutableListOf<Effect>()

    override fun audio(processor: AudioProcessor) = apply { audioEffects.add(processor) }

    override fun audio(processors: List<AudioProcessor>): EffectsBuilder = apply {
        audioEffects.addAll(processors)
    }

    override fun video(effect: Effect) = apply { videoEffects.add(effect) }

    override fun video(effects: List<Effect>): EffectsBuilder = apply {
        videoEffects.addAll(effects)
    }

    override fun build(): Effects = Effects(audioEffects, videoEffects)
}

public operator fun Effect.plus(effect: Effect): List<Effect> {
    return listOf(this, effect)
}

public operator fun AudioProcessor.plus(processor: AudioProcessor): List<AudioProcessor> {
    return listOf(this, processor)
}

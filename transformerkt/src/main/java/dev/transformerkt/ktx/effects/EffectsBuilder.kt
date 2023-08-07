package dev.transformerkt.ktx.effects

import androidx.media3.common.Effect
import androidx.media3.common.audio.AudioProcessor
import androidx.media3.transformer.Effects

public interface EffectsBuilder {

    public fun add(processor: AudioProcessor): EffectsBuilder

    public fun add(processors: List<AudioProcessor>): EffectsBuilder

    public fun add(vararg processors: AudioProcessor): EffectsBuilder = apply {
        add(processors.toList())
    }

    public fun add(effect: Effect): EffectsBuilder

    public fun add(effects: List<Effect>): EffectsBuilder

    public fun add(vararg effects: Effect): EffectsBuilder = apply {
        add(effects.toList())
    }

    public fun build(): Effects
}

internal class DefaultEffectsBuilder : EffectsBuilder {

    private val audioEffects = mutableListOf<AudioProcessor>()
    private val videoEffects = mutableListOf<Effect>()

    override fun add(processor: AudioProcessor) = apply { audioEffects.add(processor) }

    override fun add(processors: List<AudioProcessor>): EffectsBuilder = apply {
        audioEffects.addAll(processors)
    }

    override fun add(effect: Effect) = apply { videoEffects.add(effect) }

    override fun add(effects: List<Effect>): EffectsBuilder = apply {
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

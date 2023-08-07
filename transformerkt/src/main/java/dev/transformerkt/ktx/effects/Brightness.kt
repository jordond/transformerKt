package dev.transformerkt.ktx.effects

import androidx.annotation.FloatRange
import androidx.media3.effect.Brightness
import dev.transformerkt.dsl.effects.EffectsBuilder

public fun EffectsBuilder.brightness(
    @FloatRange(from = -1.0, to = 1.0) brightness: Float,
): EffectsBuilder = apply { video(Brightness(brightness)) }

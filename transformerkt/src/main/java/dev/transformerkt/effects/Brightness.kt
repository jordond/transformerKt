package dev.transformerkt.effects

import androidx.annotation.FloatRange
import androidx.media3.effect.Brightness
import dev.transformerkt.ktx.effects.EffectsBuilder

public fun EffectsBuilder.brightness(
    @FloatRange(from = -1.0, to = 1.0) brightness: Float,
): EffectsBuilder = apply { add(Brightness(brightness)) }

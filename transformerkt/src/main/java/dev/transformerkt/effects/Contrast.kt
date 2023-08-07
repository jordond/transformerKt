package dev.transformerkt.effects

import androidx.annotation.FloatRange
import androidx.media3.effect.Contrast
import dev.transformerkt.ktx.effects.EffectsBuilder

public fun EffectsBuilder.contrast(
    @FloatRange(from = -1.0, to = 1.0) contrast: Float,
): EffectsBuilder = apply { add(Contrast(contrast)) }

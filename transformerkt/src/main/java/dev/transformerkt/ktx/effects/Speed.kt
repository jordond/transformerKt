package dev.transformerkt.ktx.effects

import androidx.media3.effect.SpeedChangeEffect
import dev.transformerkt.dsl.effects.EffectsBuilder

public fun EffectsBuilder.speed(speed: Float): EffectsBuilder = apply {
    video(SpeedChangeEffect(speed))
}

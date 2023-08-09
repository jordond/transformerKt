package dev.transformerkt.ktx.effects.speed

import dev.transformerkt.dsl.effects.EffectsBuilder

public fun EffectsBuilder.speed(speed: Float): EffectsBuilder = apply {
    video(SpeedChangeEffect(speed))
}

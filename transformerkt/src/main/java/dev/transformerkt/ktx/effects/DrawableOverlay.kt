package dev.transformerkt.ktx.effects

import android.graphics.drawable.Drawable
import androidx.annotation.CheckResult
import androidx.media3.effect.DrawableOverlay
import androidx.media3.effect.OverlayEffect
import androidx.media3.effect.StaticOverlaySettings
import dev.transformerkt.dsl.effects.EffectsBuilder

@CheckResult
public fun drawableOverlayEffect(
    drawable: Drawable,
    settings: StaticOverlaySettings,
): OverlayEffect {
    val overlay = DrawableOverlay.createStaticDrawableOverlay(drawable, settings)
    return OverlayEffect(listOf(overlay))
}

public fun EffectsBuilder.drawableOverlay(
    drawable: Drawable,
    settings: StaticOverlaySettings,
): EffectsBuilder = apply {
    video(drawableOverlayEffect(drawable, settings))
}

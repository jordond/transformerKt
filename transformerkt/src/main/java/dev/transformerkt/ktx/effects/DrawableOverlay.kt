package dev.transformerkt.ktx.effects

import android.graphics.drawable.Drawable
import androidx.annotation.CheckResult
import androidx.media3.effect.DrawableOverlay
import androidx.media3.effect.OverlayEffect
import androidx.media3.effect.OverlaySettings
import dev.transformerkt.dsl.effects.EffectsBuilder
import com.google.common.collect.ImmutableList

@CheckResult
public fun drawableOverlayEffect(
    drawable: Drawable,
    settings: OverlaySettings,
): OverlayEffect {
    val overlay = DrawableOverlay.createStaticDrawableOverlay(drawable, settings)
    return OverlayEffect(ImmutableList.of(overlay))
}

public fun EffectsBuilder.drawableOverlay(
    drawable: Drawable,
    settings: OverlaySettings,
): EffectsBuilder = apply {
    video(drawableOverlayEffect(drawable, settings))
}

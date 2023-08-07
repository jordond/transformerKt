package dev.transformerkt.effects

import android.graphics.drawable.Drawable
import androidx.media3.effect.DrawableOverlay
import androidx.media3.effect.OverlayEffect
import androidx.media3.effect.OverlaySettings
import dev.transformerkt.ktx.effects.EffectsBuilder
import com.google.common.collect.ImmutableList

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
    add(drawableOverlayEffect(drawable, settings))
}

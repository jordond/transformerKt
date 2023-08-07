package dev.transformerkt.effects

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.media3.effect.BitmapOverlay
import androidx.media3.effect.OverlayEffect
import androidx.media3.effect.OverlaySettings
import dev.transformerkt.ktx.effects.EffectsBuilder
import com.google.common.collect.ImmutableList

public fun bitmapOverlayEffect(
    bitmap: Bitmap,
    settings: OverlaySettings,
): OverlayEffect {
    val overlay = BitmapOverlay.createStaticBitmapOverlay(bitmap, settings)
    return OverlayEffect(ImmutableList.of(overlay))
}

public fun bitmapOverlayEffect(
    context: Context,
    uri: Uri,
    settings: OverlaySettings,
): OverlayEffect {
    val overlay = BitmapOverlay.createStaticBitmapOverlay(context, uri, settings)
    return OverlayEffect(ImmutableList.of(overlay))
}

public fun EffectsBuilder.bitmapOverlay(
    bitmap: Bitmap,
    settings: OverlaySettings,
): EffectsBuilder = apply {
    add(bitmapOverlayEffect(bitmap, settings))
}

public fun EffectsBuilder.bitmapOverlay(
    context: Context,
    uri: Uri,
    settings: OverlaySettings,
): EffectsBuilder = apply {
    add(bitmapOverlayEffect(context, uri, settings))
}

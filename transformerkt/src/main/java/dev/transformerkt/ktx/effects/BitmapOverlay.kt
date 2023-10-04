package dev.transformerkt.ktx.effects

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.annotation.CheckResult
import androidx.annotation.DrawableRes
import androidx.media3.effect.BitmapOverlay
import androidx.media3.effect.OverlayEffect
import androidx.media3.effect.OverlaySettings
import com.google.common.collect.ImmutableList
import dev.transformerkt.dsl.effects.EffectsBuilder

@CheckResult
public fun bitmapOverlayEffect(
    bitmap: Bitmap,
    settings: OverlaySettings? = null,
): OverlayEffect {
    val overlay =
        if (settings == null) BitmapOverlay.createStaticBitmapOverlay(bitmap)
        else BitmapOverlay.createStaticBitmapOverlay(bitmap, settings)
    return OverlayEffect(ImmutableList.of(overlay))
}

@CheckResult
public fun bitmapOverlayEffect(
    context: Context,
    uri: Uri,
    settings: OverlaySettings,
): OverlayEffect {
    val overlay = BitmapOverlay.createStaticBitmapOverlay(context, uri, settings)
    return OverlayEffect(ImmutableList.of(overlay))
}

@CheckResult
public fun bitmapOverlayEffect(
    context: Context,
    @DrawableRes drawableResId: Int,
    settings: OverlaySettings? = null,
): OverlayEffect {
    val bitmap = BitmapFactory.decodeResource(context.resources, drawableResId)
    return bitmapOverlayEffect(bitmap, settings)
}

public fun EffectsBuilder.bitmapOverlay(
    bitmap: Bitmap,
    settings: OverlaySettings? = null,
): EffectsBuilder = apply {
    video(bitmapOverlayEffect(bitmap, settings))
}

public fun EffectsBuilder.bitmapOverlay(
    context: Context,
    uri: Uri,
    settings: OverlaySettings,
): EffectsBuilder = apply {
    video(bitmapOverlayEffect(context, uri, settings))
}

public fun EffectsBuilder.bitmapOverlay(
    context: Context,
    @DrawableRes drawableResId: Int,
    settings: OverlaySettings? = null,
): EffectsBuilder = apply {
    video(bitmapOverlayEffect(context, drawableResId, settings))
}

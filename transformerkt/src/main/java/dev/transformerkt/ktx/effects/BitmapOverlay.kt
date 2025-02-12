package dev.transformerkt.ktx.effects

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.annotation.CheckResult
import androidx.annotation.DrawableRes
import androidx.media3.effect.BitmapOverlay
import androidx.media3.effect.OverlayEffect
import androidx.media3.effect.StaticOverlaySettings
import dev.transformerkt.dsl.effects.EffectsBuilder

@CheckResult
public fun bitmapOverlayEffect(
    bitmap: Bitmap,
    settings: StaticOverlaySettings? = null,
): OverlayEffect {
    val overlay =
        if (settings == null) BitmapOverlay.createStaticBitmapOverlay(bitmap)
        else BitmapOverlay.createStaticBitmapOverlay(bitmap, settings)
    return OverlayEffect(listOf(overlay))
}

@CheckResult
public fun bitmapOverlayEffect(
    context: Context,
    uri: Uri,
    settings: StaticOverlaySettings,
): OverlayEffect {
    val overlay = BitmapOverlay.createStaticBitmapOverlay(context, uri, settings)
    return OverlayEffect(listOf(overlay))
}

@CheckResult
public fun bitmapOverlayEffect(
    context: Context,
    @DrawableRes drawableResId: Int,
    settings: StaticOverlaySettings? = null,
): OverlayEffect {
    val bitmap = BitmapFactory.decodeResource(context.resources, drawableResId)
    return bitmapOverlayEffect(bitmap, settings)
}

public fun EffectsBuilder.bitmapOverlay(
    bitmap: Bitmap,
    settings: StaticOverlaySettings? = null,
): EffectsBuilder = apply {
    video(bitmapOverlayEffect(bitmap, settings))
}

public fun EffectsBuilder.bitmapOverlay(
    context: Context,
    uri: Uri,
    settings: StaticOverlaySettings,
): EffectsBuilder = apply {
    video(bitmapOverlayEffect(context, uri, settings))
}

public fun EffectsBuilder.bitmapOverlay(
    context: Context,
    @DrawableRes drawableResId: Int,
    settings: StaticOverlaySettings? = null,
): EffectsBuilder = apply {
    video(bitmapOverlayEffect(context, drawableResId, settings))
}

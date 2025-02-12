package dev.transformerkt.dsl.effects

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.text.SpannableString
import androidx.media3.effect.StaticOverlaySettings
import dev.transformerkt.ktx.effects.bitmapOverlay
import dev.transformerkt.ktx.effects.textOverlay

@EffectsDsl
@Suppress("UnusedReceiverParameter")
public fun EffectsBuilder.overlaySettings(
    block: StaticOverlaySettings.Builder.() -> Unit,
): StaticOverlaySettings {
    return StaticOverlaySettings.Builder().apply(block).build()
}

public fun EffectsBuilder.bitmapOverlay(
    bitmap: Bitmap,
    block: StaticOverlaySettings.Builder.() -> Unit,
): EffectsBuilder = apply {
    bitmapOverlay(bitmap, overlaySettings(block))
}

public fun EffectsBuilder.bitmapOverlay(
    context: Context,
    uri: Uri,
    block: StaticOverlaySettings.Builder.() -> Unit,
): EffectsBuilder = apply {
    bitmapOverlay(context, uri, overlaySettings(block))
}

public fun EffectsBuilder.textOverlay(
    spannableString: SpannableString,
    block: StaticOverlaySettings.Builder.() -> Unit,
): EffectsBuilder = apply {
    textOverlay(spannableString, overlaySettings(block))
}
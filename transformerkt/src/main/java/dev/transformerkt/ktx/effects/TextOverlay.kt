package dev.transformerkt.ktx.effects

import android.text.SpannableString
import android.text.SpannableStringBuilder
import androidx.annotation.CheckResult
import androidx.core.text.toSpannable
import androidx.media3.effect.OverlayEffect
import androidx.media3.effect.StaticOverlaySettings
import androidx.media3.effect.TextOverlay
import dev.transformerkt.dsl.effects.EffectsBuilder

@CheckResult
public fun textOverlayEffect(
    text: SpannableString,
    settings: StaticOverlaySettings? = null,
): OverlayEffect {
    val overlay =
        if (settings == null) TextOverlay.createStaticTextOverlay(text)
        else TextOverlay.createStaticTextOverlay(text, settings)

    return OverlayEffect(listOf(overlay))
}

public fun EffectsBuilder.textOverlay(
    text: SpannableString,
    settings: StaticOverlaySettings? = null,
): EffectsBuilder = apply {
    video(textOverlayEffect(text, settings))
}

public inline fun buildSpannableString(
    builderAction: SpannableStringBuilder.() -> Unit,
): SpannableString {
    val builder = SpannableStringBuilder().apply(builderAction)
    return SpannableString.valueOf(builder.toSpannable())
}
package dev.transformerkt.ktx.effects

import android.text.SpannableString
import androidx.annotation.CheckResult
import androidx.media3.effect.OverlayEffect
import androidx.media3.effect.OverlaySettings
import androidx.media3.effect.TextOverlay
import com.google.common.collect.ImmutableList
import dev.transformerkt.dsl.effects.EffectsBuilder

@CheckResult
public fun textOverlayEffect(
    text: SpannableString,
    settings: OverlaySettings? = null,
): OverlayEffect {
    val overlay =
        if (settings == null) TextOverlay.createStaticTextOverlay(text)
        else TextOverlay.createStaticTextOverlay(text, settings)

    return OverlayEffect(ImmutableList.of(overlay))
}

public fun EffectsBuilder.textOverlay(
    text: SpannableString,
    settings: OverlaySettings? = null,
): EffectsBuilder = apply {
    video(textOverlayEffect(text, settings))
}

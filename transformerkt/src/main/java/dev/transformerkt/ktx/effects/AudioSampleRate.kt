package dev.transformerkt.ktx.effects

import androidx.annotation.CheckResult
import androidx.media3.common.audio.AudioProcessor
import androidx.media3.common.audio.SonicAudioProcessor
import dev.transformerkt.dsl.effects.EffectsBuilder

@CheckResult
public fun audioSampleRateEffect(
    sampleRateHz: Int,
): AudioProcessor {
    return SonicAudioProcessor().apply {
        setOutputSampleRateHz(sampleRateHz)
    }
}

public fun EffectsBuilder.audioSampleRate(
    sampleRateHz: Int,
): EffectsBuilder = apply {
    audio(audioSampleRateEffect(sampleRateHz))
}
package dev.transformerkt.ktx.effects

import androidx.annotation.CheckResult
import androidx.media3.common.audio.AudioProcessor
import androidx.media3.common.audio.ChannelMixingAudioProcessor
import androidx.media3.common.audio.ChannelMixingMatrix
import dev.transformerkt.dsl.effects.EffectsBuilder

@CheckResult
public fun volumeEffect(
    volume: Float,
    inputChannels: Int,
    outputChannels: Int = 2,
): AudioProcessor {
    val matrix = ChannelMixingMatrix.create(inputChannels, outputChannels).scaleBy(volume)
    return ChannelMixingAudioProcessor().apply { putChannelMixingMatrix(matrix) }
}

public fun EffectsBuilder.volume(
    volume: Float,
    inputChannels: Int,
    outputChannels: Int = 2,
): EffectsBuilder = apply {
    audio(volumeEffect(volume, inputChannels, outputChannels))
}

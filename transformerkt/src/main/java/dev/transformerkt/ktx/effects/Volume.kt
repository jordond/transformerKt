package dev.transformerkt.ktx.effects

import androidx.media3.common.audio.AudioProcessor
import androidx.media3.common.audio.ChannelMixingAudioProcessor
import androidx.media3.common.audio.ChannelMixingMatrix
import dev.transformerkt.dsl.effects.EffectsBuilder

public fun volumeEffect(
    volume: Float,
    inputChannels: Int,
    outputChannels: Int = 2,
): AudioProcessor {
    val matrix = ChannelMixingMatrix.create(inputChannels, outputChannels).scaleBy(volume)
    return ChannelMixingAudioProcessor().apply { putChannelMixingMatrix(matrix) }
}

// TODO: Verify 1f is 100% and 0 is 0%, and not -1 to 1
public fun EffectsBuilder.volume(
    volume: Float,
    inputChannels: Int,
    outputChannels: Int = 2,
): EffectsBuilder = apply {
    audio(volumeEffect(volume, inputChannels, outputChannels))
}

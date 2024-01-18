package dev.transformerkt.ktx.effects

import androidx.annotation.CheckResult
import dev.transformerkt.dsl.effects.EffectsBuilder
import dev.transformerkt.effects.VolumeChangeProcessor
import dev.transformerkt.effects.VolumeChangeProvider

/**
 * Creates an [VolumeChangeProcessor] that you can use to change the volume of an audio stream over time.
 *
 * @param[inputChannels] The number of input channels.
 * @param[outputChannels] The number of output channels (defaults to stereo - 2).
 * @param[volumeChangeProvider] The [VolumeChangeProvider] that will provide the volume change over time.
 */
@CheckResult
public fun volumeChangeEffect(
    inputChannels: Int,
    outputChannels: Int = 2,
    volumeChangeProvider: VolumeChangeProvider,
): VolumeChangeProcessor = VolumeChangeProcessor(
    inputChannels = inputChannels,
    outputChannels = outputChannels,
    volumeChangeProvider = volumeChangeProvider,
)

/**
 * Add [VolumeChangeProcessor] effect that you can use to change the volume of the audio
 * stream over time.
 *
 * @param[inputChannels] The number of input channels.
 * @param[outputChannels] The number of output channels (defaults to stereo - 2).
 * @param[volumeChangeProvider] The [VolumeChangeProvider] that will provide the volume change over time.
 */
public fun EffectsBuilder.volumeChange(
    inputChannels: Int,
    outputChannels: Int = 2,
    volumeChangeProvider: VolumeChangeProvider,
): EffectsBuilder = apply {
    audio(
        volumeChangeEffect(
            inputChannels = inputChannels,
            outputChannels = outputChannels,
            volumeChangeProvider = volumeChangeProvider,
        )
    )
}

/**
 * Creates an [VolumeChangeProcessor] that you can use to change the volume of an audio stream over time.
 *
 * @param[inputChannels] The number of input channels.
 * @param[outputChannels] The number of output channels (defaults to stereo - 2).
 * @param[volume] The initial volume of the audio stream.
 * @param[volumeChange] The function that will provide the volume change over time.
 */
@CheckResult
public fun volumeChangeEffect(
    inputChannels: Int,
    volume: Float = 1f,
    outputChannels: Int = 2,
    volumeChange: VolumeChangeProvider.(timeUs: Long) -> Float = { volume },
): VolumeChangeProcessor = VolumeChangeProcessor(
    inputChannels = inputChannels,
    outputChannels = outputChannels,
    volumeChangeProvider = object : VolumeChangeProvider {
        override val initial: Float = volume
        override fun getVolume(timeUs: Long): Float = volumeChange(timeUs)
    }
)

/**
 * Add [VolumeChangeProcessor] effect that you can use to change the volume of the audio
 * stream over time.
 *
 * @param[inputChannels] The number of input channels.
 * @param[outputChannels] The number of output channels (defaults to stereo - 2).
 * @param[volume] The initial volume of the audio stream.
 * @param[volumeChange] The function that will provide the volume change over time.
 */
public fun EffectsBuilder.volumeChange(
    inputChannels: Int,
    volume: Float = 1f,
    outputChannels: Int = 2,
    volumeChange: VolumeChangeProvider.(timeUs: Long) -> Float = { volume },
): EffectsBuilder = apply {
    audio(
        volumeChangeEffect(
            inputChannels = inputChannels,
            outputChannels = outputChannels,
            volume = volume,
            volumeChange = volumeChange,
        )
    )
}
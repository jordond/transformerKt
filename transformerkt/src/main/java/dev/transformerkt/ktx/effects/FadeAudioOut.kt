package dev.transformerkt.ktx.effects

import dev.transformerkt.dsl.effects.EffectsBuilder
import dev.transformerkt.effects.VolumeChangeProcessor
import dev.transformerkt.effects.VolumeChangeProvider
import java.util.concurrent.TimeUnit

/**
 * Creates an [VolumeChangeProvider] that you can use to fade the audio out at the end of the video.
 *
 * @param[totalDurationUs] The total duration of the video in microseconds.
 * @param[inputChannels] The number of input channels.
 * @param[outputChannels] The number of output channels (defaults to stereo - 2).
 * @param[initialVolume] The initial volume of the audio stream.
 * @param[finalVolume] The final volume of the audio stream.
 * @param[fadeDurationUs] The duration of the fade out in microseconds.
 */
public fun fadeAudioOutEffect(
    totalDurationUs: Long,
    inputChannels: Int,
    outputChannels: Int = 2,
    initialVolume: Float = 1f,
    finalVolume: Float = 0f,
    fadeDurationUs: Long = TimeUnit.SECONDS.toMicros(1),
): VolumeChangeProcessor {
    val provider = fadeAudioOutProvider(
        totalDurationUs = totalDurationUs,
        initialVolume = initialVolume,
        finalVolume = finalVolume,
        fadeDurationUs = fadeDurationUs,
    )
    return volumeChangeEffect(
        inputChannels = inputChannels,
        outputChannels = outputChannels,
        volumeChangeProvider = provider,
    )
}

/**
 * Add an [VolumeChangeProcessor] to the [EffectsBuilder] that you can use to fade the audio out at
 * the end of the video.
 *
 * @param[totalDurationUs] The total duration of the video in microseconds.
 * @param[inputChannels] The number of input channels.
 * @param[outputChannels] The number of output channels (defaults to stereo - 2).
 * @param[initialVolume] The initial volume of the audio stream.
 * @param[finalVolume] The final volume of the audio stream.
 * @param[fadeDurationUs] The duration of the fade out in microseconds.
 */
public fun EffectsBuilder.fadeAudioOut(
    totalDurationUs: Long,
    inputChannels: Int,
    outputChannels: Int = 2,
    initialVolume: Float = 1f,
    finalVolume: Float = 0f,
    fadeDurationUs: Long = TimeUnit.SECONDS.toMicros(1),
): EffectsBuilder = apply {
    audio(
        fadeAudioOutEffect(
            totalDurationUs = totalDurationUs,
            inputChannels = inputChannels,
            outputChannels = outputChannels,
            initialVolume = initialVolume,
            finalVolume = finalVolume,
            fadeDurationUs = fadeDurationUs,
        )
    )
}

/**
 * Creates an [VolumeChangeProvider] that you can use to fade the audio out at the end of the video.
 *
 * @param[totalDurationUs] The total duration of the video in microseconds.
 * @param[initialVolume] The initial volume of the audio stream.
 * @param[finalVolume] The final volume of the audio stream.
 * @param[fadeDurationUs] The duration of the fade out in microseconds.
 */
private fun fadeAudioOutProvider(
    totalDurationUs: Long,
    initialVolume: Float,
    finalVolume: Float,
    fadeDurationUs: Long,
): VolumeChangeProvider = object : VolumeChangeProvider {

    override val initial: Float = initialVolume

    override fun getVolume(timeUs: Long): Float = fadeAudioOut(
        totalDurationUs = totalDurationUs,
        fadeDurationUs = fadeDurationUs,
        currentTimeUs = timeUs,
        finalVolume = finalVolume,
    )
}

/**
 * A convenience function that calculates the volume value in order to fade the audio out at the
 * end of the video.
 *
 * @param[totalDurationUs] The total duration of the video in microseconds.
 * @param[fadeDurationUs] The duration of the fade out in microseconds.
 * @param[currentTimeUs] The current time of the video in microseconds.
 * @param[finalVolume] The final volume of the audio stream.
 */
private fun VolumeChangeProvider.fadeAudioOut(
    totalDurationUs: Long,
    fadeDurationUs: Long,
    currentTimeUs: Long,
    finalVolume: Float,
): Float {
    val fadeOutStart = totalDurationUs - fadeDurationUs
    return if (currentTimeUs < fadeOutStart) initial
    else {
        val fadeoutElapsedTime = currentTimeUs - fadeOutStart
        val fadeOutProgress = fadeoutElapsedTime.toFloat() / fadeDurationUs.toFloat()
        initial - (initial - finalVolume) * fadeOutProgress
    }
}
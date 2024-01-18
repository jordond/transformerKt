package dev.transformerkt.effects

import androidx.media3.common.C
import androidx.media3.common.audio.AudioMixingUtil
import androidx.media3.common.audio.AudioProcessor
import androidx.media3.common.audio.BaseAudioProcessor
import androidx.media3.common.audio.ChannelMixingAudioProcessor
import androidx.media3.common.audio.ChannelMixingMatrix
import androidx.media3.common.util.Util
import java.nio.ByteBuffer

/**
 * An AudioProcessor that handles changing the volume on an audio channel.
 *
 * Input and output are 16-bit PCM.
 *
 * Inspired by [ChannelMixingAudioProcessor].
 *
 * @param[inputChannels] The number of input channels.
 * @param[outputChannels] The number of output channels.
 * @param[volumeChangeProvider] A provider that returns the volume for a given time.
 */
public class VolumeChangeProcessor(
    inputChannels: Int,
    outputChannels: Int,
    private val volumeChangeProvider: VolumeChangeProvider,
) : BaseAudioProcessor() {

    private var bytesRead: Long = 0
    private var currentVolume: Float = 1f

    private var channelMixingMatrix = ChannelMixingMatrix
        .create(inputChannels, outputChannels)
        .scaleBy(volumeChangeProvider.initial)

    override fun onConfigure(
        inputAudioFormat: AudioProcessor.AudioFormat,
    ): AudioProcessor.AudioFormat {
        if (inputAudioFormat.encoding != C.ENCODING_PCM_16BIT) {
            throw AudioProcessor.UnhandledAudioFormatException(inputAudioFormat)
        }

        return AudioProcessor.AudioFormat(
            /* sampleRate = */ inputAudioFormat.sampleRate,
            /* channelCount = */ channelMixingMatrix.outputChannelCount,
            /* encoding = */ C.ENCODING_PCM_16BIT,
        )
    }

    override fun queueInput(inputBuffer: ByteBuffer) {
        bytesRead += inputBuffer.remaining()

        val timeUs = Util.scaleLargeTimestamp(
            /* timestamp = */ bytesRead,
            /* multiplier = */ C.MICROS_PER_SECOND,
            /* divisor = */ inputAudioFormat.sampleRate.toLong() * inputAudioFormat.bytesPerFrame
        )

        val newVolume: Float = volumeChangeProvider.getVolume(timeUs)
        check(newVolume >= 0f) { "Volume must not be negative, received: $newVolume" }

        if (newVolume != currentVolume) {
            currentVolume = newVolume
            channelMixingMatrix = channelMixingMatrix.scaleBy(newVolume)
            flush()
        }

        val framesToMix = inputBuffer.remaining() / inputAudioFormat.bytesPerFrame
        val outputBuffer = replaceOutputBuffer(framesToMix * outputAudioFormat.bytesPerFrame)
        AudioMixingUtil.mix(
            /* inputBuffer = */ inputBuffer,
            /* inputAudioFormat = */ inputAudioFormat,
            /* mixingBuffer = */ outputBuffer,
            /* mixingAudioFormat = */ outputAudioFormat,
            /* matrix = */ channelMixingMatrix,
            /* framesToMix = */ framesToMix,
            /* accumulate= */ false
        )
        outputBuffer.flip()
    }

    override fun onReset() {
        currentVolume = 1f
        bytesRead = 0
    }
}

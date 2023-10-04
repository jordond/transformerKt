package dev.transformerkt.demo.transformer

import android.content.Context
import android.media.MediaExtractor
import android.media.MediaFormat
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.transformer.Composition
import androidx.media3.transformer.Composition.HDR_MODE_TONE_MAP_HDR_TO_SDR_USING_OPEN_GL
import androidx.media3.transformer.EditedMediaItemSequence
import androidx.media3.transformer.Transformer
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.transformerkt.TransformerKt
import dev.transformerkt.TransformerStatus
import dev.transformerkt.demo.processor.model.VideoDetails
import dev.transformerkt.demo.ui.effects.EffectSettings
import dev.transformerkt.dsl.composition.compositionOf
import dev.transformerkt.dsl.effects.bitmapOverlay
import dev.transformerkt.dsl.effects.setEffects
import dev.transformerkt.ktx.asEdited
import dev.transformerkt.ktx.buildWith
import dev.transformerkt.ktx.effects.audioSampleRate
import dev.transformerkt.ktx.effects.brightness
import dev.transformerkt.ktx.effects.contrast
import dev.transformerkt.ktx.effects.speed
import dev.transformerkt.ktx.effects.volume
import dev.transformerkt.ktx.inputs.start
import dev.transformerkt.ktx.setClippingConfiguration
import kotlinx.coroutines.flow.Flow
import java.io.File
import javax.inject.Inject

class TransformerRepo @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    private val transformer = Transformer.Builder(context).build()

    suspend fun convertToSdr(
        input: VideoDetails,
        onProgress: (TransformerStatus.Progress) -> Unit,
    ): TransformerStatus.Finished {
        val output = hdrToSdrOutput(context)
        val request = TransformerKt.H264Request.buildWith {
            setHdrMode(HDR_MODE_TONE_MAP_HDR_TO_SDR_USING_OPEN_GL)
            setAudioMimeType(MimeTypes.AUDIO_AAC)
        }

        return transformer.start(input.uri, output, request) { progress ->
            onProgress(TransformerStatus.Progress(progress))
        }
    }

    fun trimVideo(
        input: VideoDetails,
        startMs: Long,
        endMs: Long,
    ): Flow<TransformerStatus> {
        val output = trimOutput(context)
        val request = TransformerKt.DefaultRequest
        val mediaItem = MediaItem.fromUri(input.uri).buildWith {
            setClippingConfiguration(startMs = startMs, endMs = endMs)
        }

        return transformer.start(mediaItem, output, request)
    }

    fun concat(inputs: List<VideoDetails>): Flow<TransformerStatus> {
        val mediaItems = inputs.map { MediaItem.fromUri(it.uri).asEdited() }
        val sequence = EditedMediaItemSequence(mediaItems)
        val composition = Composition.Builder(listOf(sequence)).build()

        val output = concatOutput(context)
        val request = TransformerKt.DefaultRequest
        return transformer.start(composition, output, request)
    }

    fun transform(
        videos: List<VideoDetails>,
        settings: EffectSettings,
    ): Flow<TransformerStatus> {
        val composition = compositionOf {
            sequenceOf(videos) { video ->
                item(video.uri) {
                    if (settings.speed != 1f) {
                        setRemoveAudio(true)
                    }

                    setEffects {
                        val inputChannels = video.uri.audioTracks(context)
                        if (settings.speed == 1f) {
                            if (settings.volume != 1f) {
                                volume(settings.volume, inputChannels = inputChannels)
                            }
                            if (settings.audioOverlay != null) {
                                audioSampleRate(44100)
                            }
                        } else {
                            speed(settings.speed)
                        }

                        if (settings.brightness != 0f) {
                            brightness(settings.brightness)
                        }

                        if (settings.contrast != 0f) {
                            contrast(settings.contrast)
                        }

                        if (settings.overlay != null) {
                            bitmapOverlay(context, settings.overlay) {
                                setScale(.2f, .2f)
                                setOverlayFrameAnchor(.8f, .8f)
                            }
                        }
                    }
                }
            }

            if (settings.audioOverlay != null) {
                add(isLooping = true) {
                    val inputChannels = settings.audioOverlay.uri.audioTracks(context)
                    item(settings.audioOverlay.uri) {
                        setEffects {
                            audioSampleRate(44100)
                            volume(settings.audioOverlay.volume, inputChannels = inputChannels)
                        }
                    }
                }
            }
        }

        val output = transformOutput(context)
        return transformer.start(composition, output, TransformerKt.DefaultRequest)
    }

    private fun Uri.audioTracks(context: Context): Int {
        val e = MediaExtractor().apply { this.setDataSource(context, this@audioTracks, null) }
        val audioFormat = List(e.trackCount) { e.getTrackFormat(it) }.firstOrNull { format ->
            (format.getString("mime") ?: "").startsWith("audio/")
        } ?: return 0

        return audioFormat.getInteger(MediaFormat.KEY_CHANNEL_COUNT).also { e.release() }
    }

    companion object {

        fun hdrToSdrOutput(context: Context) =
            File(context.cacheDir, "hdr_to_sdr_output.mp4")

        fun trimOutput(context: Context) =
            File(context.cacheDir, "trim_output.mp4")

        fun concatOutput(context: Context) =
            File(context.cacheDir, "concat_output.mp4")

        fun transformOutput(context: Context) =
            File(context.cacheDir, "transform_output.mp4")
    }
}
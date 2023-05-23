package dev.transformerkt.demo.transformer

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.transformer.TransformationRequest
import androidx.media3.transformer.Transformer
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.transformerkt.TransformerKt
import dev.transformerkt.TransformerStatus
import dev.transformerkt.demo.processor.model.VideoDetails
import dev.transformerkt.ktx.buildWith
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
            setHdrMode(TransformationRequest.HDR_MODE_TONE_MAP_HDR_TO_SDR_USING_OPEN_GL)
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

    companion object {

        fun hdrToSdrOutput(context: Context) =
            File(context.cacheDir, "hdr_to_sdr_output.mp4")

        fun trimOutput(context: Context) =
            File(context.cacheDir, "trim_output.mp4")
    }
}
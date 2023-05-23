package dev.transformerkt.demo.transformer

import android.content.Context
import androidx.media3.common.MimeTypes
import androidx.media3.transformer.TransformationRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.transformerkt.TransformerKt
import dev.transformerkt.demo.processor.model.VideoDetails
import dev.transformerkt.ktx.buildWith
import dev.transformerkt.ktx.inputs.asTransformerInput
import java.io.File
import javax.inject.Inject

class TransformerRepo @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    private val transformer = TransformerKt.create(context)

    suspend fun convertToSdr(
        input: VideoDetails,
        onProgress: (TransformerKt.Status.Progress) -> Unit,
    ): TransformerKt.Status.Finished {
        val output = hdrToSdrOutput(context)
        val request = TransformerKt.H264Request.buildWith {
            setHdrMode(TransformationRequest.HDR_MODE_TONE_MAP_HDR_TO_SDR_USING_OPEN_GL)
            setAudioMimeType(MimeTypes.AUDIO_AAC)
        }

        return transformer.start(input.uri.asTransformerInput(), output, request) { progress ->
            onProgress(TransformerKt.Status.Progress(progress))
        }
    }

    companion object {

        fun hdrToSdrOutput(context: Context) = File(context.cacheDir, "hdr_to_sdr_output.mp4")
    }
}
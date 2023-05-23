package dev.transformerkt.demo.processor

import android.content.Context
import android.media.MediaExtractor
import android.media.MediaFormat
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.transformerkt.demo.processor.model.ColorStandard
import dev.transformerkt.demo.processor.model.ColorTransfer
import dev.transformerkt.demo.processor.model.VideoDetails
import javax.inject.Inject

class MediaExtractorVideoProcessor @Inject constructor(
    @ApplicationContext private val context: Context,
) : VideoProcessor {

    override suspend fun process(input: Uri): VideoDetails {
        val extractor = MediaExtractor().also { it.setDataSource(context, input, null) }
        val videoTrackIndex = extractor.findTrackByMimeType("video/")
            ?: throw RuntimeException("No video found!")

        extractor.selectTrack(videoTrackIndex)
        val format = extractor.getTrackFormat(videoTrackIndex)

        val standard = format.getString(MediaFormat.KEY_COLOR_STANDARD)
        val colorStandard = ColorStandard.from(standard?.toIntOrNull())

        val transfer = format.getString(MediaFormat.KEY_COLOR_TRANSFER)
        val colorTransfer = ColorTransfer.from(transfer?.toIntOrNull())

        val duration = format.getLong(MediaFormat.KEY_DURATION).takeIf { it > 0 }
            ?: throw RuntimeException("Invalid duration!")

        extractor.release()

        return VideoDetails(
            uri = input,
            colorStandard = colorStandard,
            colorTransfer = colorTransfer,
            duration = duration,
        )
    }

    private fun MediaExtractor.findTrackByMimeType(mimeType: String): Int? {
        for (index in 0 until trackCount) {
            val mime = getTrackFormat(index).getString(MediaFormat.KEY_MIME) ?: ""
            if (mime.startsWith(mimeType)) {
                return index
            }
        }

        return null
    }
}
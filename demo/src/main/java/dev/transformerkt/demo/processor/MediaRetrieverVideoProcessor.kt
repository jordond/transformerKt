package dev.transformerkt.demo.processor

import android.content.Context
import android.media.MediaMetadataRetriever
import android.media.MediaMetadataRetriever.*
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.transformerkt.demo.processor.model.ColorStandard
import dev.transformerkt.demo.processor.model.ColorTransfer
import dev.transformerkt.demo.processor.model.VideoDetails
import io.github.aakira.napier.Napier
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.R)
class MediaRetrieverVideoProcessor @Inject constructor(
    @ApplicationContext private val context: Context,
) : VideoProcessor {

    override suspend fun process(input: Uri): VideoDetails {
        val retriever = MediaMetadataRetriever().also { it.setDataSource(context, input) }
        retriever.extract(METADATA_KEY_HAS_VIDEO)
            ?: throw RuntimeException("No video found!")

        val standard = retriever.extract(METADATA_KEY_COLOR_STANDARD)
        val colorStandard = ColorStandard.from(standard?.toIntOrNull())

        val transfer = retriever.extract(METADATA_KEY_COLOR_TRANSFER)
        val colorTransfer = ColorTransfer.from(transfer?.toIntOrNull())

        val duration = retriever.extract(METADATA_KEY_DURATION)?.toLongOrNull()
            ?: throw RuntimeException("No duration found!")

        retriever.release()

        return VideoDetails(
            uri = input,
            colorStandard = colorStandard,
            colorTransfer = colorTransfer,
            duration = duration,
        )
    }
}

private fun MediaMetadataRetriever.extract(key: Int): String? {
    return try {
        extractMetadata(key)
    } catch (cause: Throwable) {
        Napier.d { "Failed to extract metadata: $cause" }
        null
    }
}
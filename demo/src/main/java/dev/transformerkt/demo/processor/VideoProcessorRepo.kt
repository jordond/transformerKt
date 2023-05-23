package dev.transformerkt.demo.processor

import android.net.Uri
import dev.transformerkt.demo.processor.model.VideoDetails
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class VideoProcessorRepo @Inject constructor(
    private val videoProcessor: VideoProcessor,
) {

    private val cachedResults = mutableMapOf<Uri, VideoDetails>()

    suspend fun process(input: Uri): VideoProcessor.Result {
        if (cachedResults.containsKey(input)) {
            return VideoProcessor.Result.Success(cachedResults[input]!!)
        }

        return try {
            val details = withContext(Dispatchers.IO) {
                videoProcessor.process(input)
            }

            cachedResults[input] = details
            VideoProcessor.Result.Success(details)
        } catch (cause: Throwable) {
            if (cause is CancellationException) throw cause

            VideoProcessor.Result.Failure.from(cause)
        }
    }
}
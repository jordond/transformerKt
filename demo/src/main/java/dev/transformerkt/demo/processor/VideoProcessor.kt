package dev.transformerkt.demo.processor

import android.net.Uri
import dev.transformerkt.demo.processor.model.VideoDetails

interface VideoProcessor {

    suspend fun process(input: Uri): VideoDetails

    sealed interface Result {

        data class Success(val details: VideoDetails) : Result
        sealed class Failure : Throwable(), Result {
            data class Unknown(override val cause: Throwable) : Failure()

            companion object {

                internal fun from(cause: Throwable): Failure = when (cause) {
                    is Failure -> cause
                    else -> Unknown(cause)
                }
            }
        }
    }
}



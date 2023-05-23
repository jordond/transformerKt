package dev.transformerkt.demo.ui.trim

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.transformerkt.TransformerStatus
import dev.transformerkt.demo.processor.VideoProcessor
import dev.transformerkt.demo.processor.VideoProcessorRepo
import dev.transformerkt.demo.processor.model.VideoDetails
import dev.transformerkt.demo.transformer.TransformerRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.roundToLong

@HiltViewModel
class TrimModel @Inject constructor(
    private val videoProcessorRepo: VideoProcessorRepo,
    private val transformerRepo: TransformerRepo,
) : ViewModel() {

    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()

    private var trimJob: Job? = null

    fun selectUri(uri: Uri) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                processVideo(uri)
            }
        }
    }

    fun updateLength(length: Long) {
        val duration = state.value.selectedVideo?.duration ?: return

        val max = ((duration - TrimStep) / 1000.0).roundToLong() * 1000
        val newLength = length.coerceIn(TrimMin..max)
        _state.update { it.copy(trimLength = newLength) }
    }

    fun trim() {
        trimJob = viewModelScope.launch {
            withContext(Dispatchers.IO) {
                doTrim()
                trimJob = null
            }
        }
    }

    fun cancel() {
        trimJob?.cancel()
        trimJob = null
        _state.update { state ->
            state.copy(
                trimming = false,
                trimResult = null,
            )
        }
    }

    private suspend fun processVideo(uri: Uri) {
        _state.update { state ->
            state.copy(
                processing = true,
                processingFailed = null,
                trimResult = null,
            )
        }

        when (val processed = videoProcessorRepo.process(uri)) {
            is VideoProcessor.Result.Success -> _state.update { state ->
                state.copy(selectedVideo = processed.details, processing = false)
            }
            is VideoProcessor.Result.Failure -> _state.update { state ->
                state.copy(
                    processing = false,
                    processingFailed = processed.message,
                    selectedVideo = null,
                )
            }
        }
    }

    private suspend fun doTrim() {
        val video = state.value.selectedVideo ?: return
        _state.update { it.copy(trimming = true) }

        val (startMs, endMs) = video.middleOffsets(desiredLengthMs = 1000L)

        transformerRepo.trimVideo(
            input = video,
            startMs = startMs,
            endMs = endMs,
        ).collect { status ->
            _state.update { state ->
                state.copy(
                    trimming = status !is TransformerStatus.Finished,
                    trimResult = status,
                )
            }
        }
    }

    private fun VideoDetails.middleOffsets(desiredLengthMs: Long = 1000L): Pair<Long, Long> {
        check(desiredLengthMs <= duration) {
            "Desired length ($desiredLengthMs) must be less than video duration ($duration)"
        }

        val halfDuration = desiredLengthMs / 2
        val middle = duration / 2
        val start = middle - halfDuration

        return start to start + desiredLengthMs
    }

    data class State(
        val processing: Boolean = false,
        val processingFailed: String? = null,
        val selectedVideo: VideoDetails? = null,
        val trimLength: Long = TrimMin,
        val trimming: Boolean = false,
        val trimResult: TransformerStatus? = null,
    ) {

        val canSelect = !processing && !trimming
        val canTrim = !processing && processingFailed == null
            && selectedVideo != null && !trimming
        val trimLengthSeconds = trimLength / 1000.0
    }

    companion object {

        const val TrimMin = 1000L
        const val TrimMax = 10_000L
        const val TrimStep = 500L
    }
}
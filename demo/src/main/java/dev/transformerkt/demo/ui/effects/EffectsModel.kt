package dev.transformerkt.demo.ui.effects

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.transformerkt.TransformerStatus
import dev.transformerkt.demo.processor.VideoProcessor
import dev.transformerkt.demo.processor.VideoProcessorRepo
import dev.transformerkt.demo.processor.model.VideoDetails
import dev.transformerkt.demo.transformer.TransformerRepo
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class EffectsModel @Inject constructor(
    private val videoProcessorRepo: VideoProcessorRepo,
    private val transformerRepo: TransformerRepo,
) : ViewModel() {

    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()

    private var job: Job? = null

    fun selectUri(uris: List<Uri>) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                processVideo(uris)
            }
        }
    }

    fun updateSettings(settings: EffectSettings) {
        _state.update { state ->
            state.copy(settings = settings)
        }
    }

    fun selectAudioUri(uri: Uri) {
        _state.update { state ->
            val audio = state.settings.audioOverlay?.copy(uri = uri) ?: AudioOverlay(uri)
            state.copy(settings = state.settings.copy(audioOverlay = audio))
        }
    }

    fun start() {
        job = viewModelScope.launch {
            withContext(Dispatchers.IO) {
                doWork()
                job = null
            }
        }
    }

    fun cancel() {
        job?.cancel()
        job = null
        _state.update { state ->
            state.copy(
                inProgress = false,
                result = null,
            )
        }
    }

    private suspend fun processVideo(uris: List<Uri>) {
        _state.update { state ->
            state.copy(
                processing = true,
                processingFailed = null,
                result = null,
                selectedVideos = emptyList(),
            )
        }

        for (uri in uris) {
            when (val processed = videoProcessorRepo.process(uri)) {
                is VideoProcessor.Result.Success -> _state.update { state ->
                    state.copy(selectedVideos = state.selectedVideos + processed.details)
                }
                is VideoProcessor.Result.Failure -> {
                    _state.update { state ->
                        Napier.e(processed.cause) { "Failed to process video" }
                        state.copy(
                            processing = false,
                            processingFailed = processed.message,
                            selectedVideos = emptyList(),
                        )
                    }

                    break
                }
            }
        }

        _state.update { state ->
            state.copy(processing = false)
        }
    }

    private suspend fun doWork() {
        val videos = state.value.selectedVideos.takeIf { it.isNotEmpty() } ?: return
        _state.update { it.copy(inProgress = true) }

        transformerRepo.transform(videos, state.value.settings).collect { status ->
            _state.update { state ->
                state.copy(
                    inProgress = status !is TransformerStatus.Finished,
                    result = status,
                )
            }
        }
    }

    data class State(
        val processing: Boolean = false,
        val processingFailed: String? = null,
        val selectedVideos: List<VideoDetails> = emptyList(),
        val settings: EffectSettings = EffectSettings(),
        val inProgress: Boolean = false,
        val result: TransformerStatus? = null,
    ) {

        val canSelect = !processing && !inProgress
        val canStart = !processing && processingFailed == null
            && selectedVideos.isNotEmpty() && !inProgress
        val duration: Long
            get() = selectedVideos.sumOf { it.duration }
    }
}
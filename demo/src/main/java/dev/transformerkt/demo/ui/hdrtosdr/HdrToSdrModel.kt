package dev.transformerkt.demo.ui.hdrtosdr

import android.content.Context
import android.net.Uri
import android.os.Build
import android.view.Display
import android.view.WindowManager
import androidx.core.content.getSystemService
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

@HiltViewModel
class HdrToSdrModel @Inject constructor(
    private val videoProcessorRepo: VideoProcessorRepo,
    private val transformerRepo: TransformerRepo,
) : ViewModel() {

    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()

    private var convertJob: Job? = null

    fun init(activityContext: Context) {
        val capabilities = activityContext.display()?.hdrCapabilities()
        val supportsHdr = capabilities?.isNotEmpty() == true
        _state.update { it.copy(supportsHdr = supportsHdr) }
    }

    fun selectUri(uri: Uri) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                processVideo(uri)
            }
        }
    }

    fun convert() {
        convertJob = viewModelScope.launch {
            withContext(Dispatchers.IO) {
                doConvert()
                convertJob = null
            }
        }
    }

    fun cancel() {
        convertJob?.cancel()
        convertJob = null
        _state.update { state ->
            state.copy(
                converting = false,
                convertResult = null,
            )
        }
    }

    private suspend fun processVideo(uri: Uri) {
        _state.update { state ->
            state.copy(
                processing = true,
                processingFailed = null,
                convertResult = null,
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

    private suspend fun doConvert() {
        val video = state.value.selectedVideo ?: return
        _state.update { it.copy(converting = true) }

        val result = transformerRepo.convertToSdr(video) { progress ->
            _state.update { it.copy(convertResult = progress) }
        }

        _state.update { state ->
            state.copy(
                converting = false,
                convertResult = result,
            )
        }
    }

    data class State(
        val supportsHdr: Boolean = false,
        val processing: Boolean = false,
        val processingFailed: String? = null,
        val selectedVideo: VideoDetails? = null,
        val converting: Boolean = false,
        val convertResult: TransformerStatus? = null,
    ) {

        val canSelect = !processing && !converting
        val canConvert = !processing && processingFailed == null
            && selectedVideo != null && !converting && selectedVideo.isHdr
    }
}

@Suppress("DEPRECATION")
private fun Context.display() =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) display
    else getSystemService<WindowManager>()?.defaultDisplay

@Suppress("DEPRECATION")
private fun Display.hdrCapabilities(): IntArray =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) mode.supportedHdrTypes
    else hdrCapabilities.supportedHdrTypes
@file:OptIn(ExperimentalMaterial3Api::class)

package dev.transformerkt.demo.ui.effects

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.ramcosta.composedestinations.annotation.Destination
import dev.transformerkt.TransformerStatus
import dev.transformerkt.demo.ui.components.VideoPlayer
import dev.transformerkt.demo.ui.theme.TransformerKtDemoTheme

private val videoRequest = PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
private val imageRequest = PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)

@Destination
@Composable
fun EffectsScreen() {
    val model = hiltViewModel<EffectsModel>()
    val state by model.state.collectAsStateWithLifecycle()

    EffectsContent(
        state = state,
        updateSettings = { model.updateSettings(it) },
        onSelectUri = { model.selectUri(it) },
        onSelectAudioUri = { model.selectAudioUri(it) },
        updateAudioOverlay = { model.updateAudioOverlay(it) },
        onStart = { model.start() },
        onCancel = { model.cancel() },
    )
}

@Composable
fun EffectsContent(
    state: EffectsModel.State,
    updateSettings: (EffectSettings) -> Unit,
    onSelectUri: (List<Uri>) -> Unit = {},
    onSelectAudioUri: (Uri) -> Unit = {},
    updateAudioOverlay: (AudioOverlay) -> Unit = {},
    onStart: () -> Unit = {},
    onCancel: () -> Unit = {},
) {
    val mediaPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = { uris -> onSelectUri(uris) },
    )

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> updateSettings(state.settings.copy(overlay = uri)) },
    )

    val audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> if (uri != null) onSelectAudioUri(uri) },
    )

    Scaffold(
        topBar = { TopAppBar(title = { Text(text = "Transform") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row {
                Button(
                    enabled = state.canSelect,
                    onClick = { mediaPickerLauncher.launch(videoRequest) },
                ) {
                    Text(text = "Select videos")
                }
                Button(enabled = state.canStart, onClick = onStart) {
                    Text(text = "Start")
                }
                Button(enabled = state.inProgress, onClick = onCancel) {
                    Text(text = "Cancel")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "Options:")
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(horizontal = 16.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    var volume by remember(state.settings.volume) {
                        mutableFloatStateOf(state.settings.volume)
                    }
                    Text(text = "Video Volume:", modifier = Modifier.width(100.dp))
                    Slider(
                        valueRange = 0f..1f,
                        value = volume,
                        onValueChange = { volume = it },
                        onValueChangeFinished = {
                            updateSettings(state.settings.copy(volume = volume))
                        },
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    var brightness by remember(state.settings.brightness) {
                        mutableFloatStateOf(state.settings.brightness)
                    }
                    Text(text = "Brightness:", modifier = Modifier.width(100.dp))
                    Slider(
                        valueRange = -1f..1f,
                        value = brightness,
                        onValueChange = { brightness = it },
                        onValueChangeFinished = {
                            updateSettings(state.settings.copy(brightness = brightness))
                        },
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    var contrast by remember(state.settings.contrast) {
                        mutableFloatStateOf(state.settings.contrast)
                    }
                    Text(text = "Contrast:", modifier = Modifier.width(100.dp))
                    Slider(
                        valueRange = -1f..1f,
                        value = contrast,
                        onValueChange = { contrast = it },
                        onValueChangeFinished = {
                            updateSettings(state.settings.copy(contrast = contrast))
                        },
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    var speed by remember(state.settings.speed) {
                        mutableFloatStateOf(state.settings.speed)
                    }
                    Text(text = "Speed:", modifier = Modifier.width(100.dp))
                    Text(text = String.format("%.2fx", speed))
                    Slider(
                        valueRange = -0.5f..6f,
                        steps = 16,
                        value = speed,
                        onValueChange = { speed = it },
                        onValueChangeFinished = {
                            updateSettings(state.settings.copy(speed = speed))
                        },
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    var volume by remember(state.settings.audioOverlay?.volume) {
                        mutableFloatStateOf(state.settings.audioOverlay?.volume ?: 1f)
                    }
                    Text(text = "Audio Overlay:")
                    OutlinedButton(onClick = { audioPickerLauncher.launch("audio/*") }) {
                        Text(text = "Select")
                    }
                    if (state.settings.audioOverlay != null) {
                        Slider(
                            valueRange = 0f..1f,
                            value = volume,
                            onValueChange = { volume = it },
                            onValueChangeFinished = {
                                updateAudioOverlay(state.settings.audioOverlay.copy(volume = volume))
                            },
                        )
                    }
                }

                if (state.settings.audioOverlay != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(text = "Fade Audio Overlay: ")
                        Checkbox(
                            checked = state.settings.audioOverlay.fade,
                            onCheckedChange = { value ->
                                updateAudioOverlay(state.settings.audioOverlay.copy(fade = value))
                            }
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(text = "Image Overlay:")
                    OutlinedButton(onClick = { imagePickerLauncher.launch(imageRequest) }) {
                        Text(text = "Select")
                    }
                    if (state.settings.overlay != null) {
                        AsyncImage(
                            model = state.settings.overlay,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                        )
                    }
                }
            }

            when {
                state.processing -> {
                    Text(text = "Processing...")
                    LinearProgressIndicator()
                }
                state.processingFailed != null -> {
                    Text(text = "Unable to process video!")
                    Text(
                        text = state.processingFailed,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
                state.canStart && state.inProgress.not() -> {
                    Text("Selected videos: ${state.selectedVideos.size}, duration: ${state.duration}")
                }
                state.inProgress -> {
                    Text(text = "Converting...")
                    LinearProgressIndicator()
                }
            }

            val result = state.result
            if (result != null) {
                when (result) {
                    is TransformerStatus.Failure -> {
                        Text(text = "Unable to transform video!")
                        Text(
                            text = result.cause.message ?: "Unknown error",
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                    is TransformerStatus.Progress -> {
                        Text(text = "Progress: ${result.progress}")
                        LinearProgressIndicator(progress = result.progress / 100f)
                    }
                    is TransformerStatus.Success -> {}
                }
            }

            if (!state.inProgress && result != null && result is TransformerStatus.Success) {
                var play by remember { mutableStateOf(true) }
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Result", style = MaterialTheme.typography.titleMedium)
                    VideoPlayer(
                        file = result.output,
                        play = play,
                        modifier = Modifier.height(200.dp),
                        useController = true,
                    )
                    Button(onClick = { play = !play }) {
                        Text(text = if (play) "Pause" else "Play")
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun EffectsScreenPreview() {
    var state by remember { mutableStateOf(EffectsModel.State()) }

    TransformerKtDemoTheme {
        EffectsContent(
            state = state,
            updateSettings = { state = state.copy(settings = it) },
        )
    }
}
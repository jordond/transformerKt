@file:OptIn(ExperimentalMaterial3Api::class)

package dev.transformerkt.demo.ui.concat

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import dev.transformerkt.TransformerStatus
import dev.transformerkt.demo.ui.components.VideoPlayer
import dev.transformerkt.demo.ui.effects.EffectsModel
import dev.transformerkt.demo.ui.theme.TransformerKtDemoTheme

private val request = PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)

@Destination
@Composable
fun ConcatScreen() {
    val model = hiltViewModel<EffectsModel>()
    val state by model.state.collectAsStateWithLifecycle()

    val mediaPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = { uris -> model.selectUri(uris) },
    )

    Scaffold(
        topBar = { TopAppBar(title = { Text(text = "Concat") }) }
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
                    onClick = { mediaPickerLauncher.launch(request) },
                ) {
                    Text(text = "Select videos")
                }
                Button(enabled = state.canStart, onClick = model::start) {
                    Text(text = "Start")
                }
                Button(enabled = state.inProgress, onClick = model::cancel) {
                    Text(text = "Cancel")
                }
            }

            if (state.canStart) {
                Text(text = "Options:")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
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
                        text = state.processingFailed!!,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
                state.canStart && state.inProgress.not() -> {
                    Text("Ready to Concat!")
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
                        Text(text = "Unable to concat video!")
                        Text(
                            text = result.cause.message ?: "Unknown error",
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                    is TransformerStatus.Progress -> {
                        Text(text = "Progress: ${result.progress}")
                        LinearProgressIndicator(progress = result.progress / 100f)
                    }
                    is TransformerStatus.Success -> {
                        Text(text = "Success!")
                    }
                }
            }

            if (!state.inProgress && result != null && result is TransformerStatus.Success) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Result")
                    VideoPlayer(file = result.output, modifier = Modifier.height(200.dp))
                }
            }
        }
    }
}

@Preview
@Composable
private fun ConcatScreenPreview() {
    TransformerKtDemoTheme {
        ConcatScreen()
    }
}
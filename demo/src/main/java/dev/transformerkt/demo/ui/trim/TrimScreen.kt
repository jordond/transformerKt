@file:OptIn(ExperimentalMaterial3Api::class)

package dev.transformerkt.demo.ui.trim

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import dev.transformerkt.demo.ui.theme.TransformerKtDemoTheme

private val request = PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)

@Destination
@Composable
fun TrimScreen() {
    val model = hiltViewModel<TrimModel>()
    val state by model.state.collectAsStateWithLifecycle()

    val mediaPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> uri?.let { model.selectUri(it) } },
    )

    Scaffold(
        topBar = { TopAppBar(title = { Text(text = "Trim") }) }
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
                    Text(text = "Select video")
                }
                Button(enabled = state.canTrim, onClick = model::trim) {
                    Text(text = "Start")
                }
                Button(enabled = state.trimming, onClick = model::cancel) {
                    Text(text = "Cancel")
                }
            }

            if (state.canTrim) {
                Text(text = "Options:")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(text = "Length:")
                        Spacer(modifier = Modifier.width(4.dp))
                        TextButton(
                            onClick = {
                                model.updateLength(state.trimLength - TrimModel.TrimStep)
                            },
                        ) {
                            Text(text = "-")
                        }
                        Text(text = "${state.trimLengthSeconds}s")
                        TextButton(
                            onClick = {
                                model.updateLength(state.trimLength + TrimModel.TrimStep)
                            },
                        ) {
                            Text(text = "+")
                        }
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
                        text = state.processingFailed!!,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
                state.canTrim && state.trimming.not() -> {
                    Text(text = "Ready to Trim!")
                }
                state.trimming -> {
                    Text(text = "Converting...")
                    LinearProgressIndicator()
                }
            }

            val result = state.trimResult
            if (result != null) {
                when (result) {
                    is TransformerStatus.Failure -> {
                        Text(text = "Unable to trim video!")
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

            if (!state.trimming && result != null && result is TransformerStatus.Success) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Before")
                    VideoPlayer(uri = state.selectedVideo!!.uri, modifier = Modifier.height(200.dp))

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Trimmed")
                    VideoPlayer(file = result.output, modifier = Modifier.height(200.dp))
                }
            }
        }
    }
}

@Preview
@Composable
fun TrimScreenPreview() {
    TransformerKtDemoTheme {
        TrimScreen()
    }
}
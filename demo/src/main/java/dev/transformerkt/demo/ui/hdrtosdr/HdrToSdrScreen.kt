@file:OptIn(ExperimentalMaterial3Api::class)

package dev.transformerkt.demo.ui.hdrtosdr

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import dev.transformerkt.TransformerKt
import dev.transformerkt.demo.ui.components.VideoPlayer

private val request = PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)

@RootNavGraph(start = true)
@Destination
@Composable
fun HdrToSdrScreen() {
    val model = hiltViewModel<HdrToSdrModel>()
    val state by model.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    SideEffect {
        model.init(context)
    }

    val mediaPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> uri?.let { model.selectUri(it) } },
    )

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("HDR to SDR") })
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (state.supportsHdr) {
                Row {
                    Button(
                        enabled = state.canSelect,
                        onClick = { mediaPickerLauncher.launch(request) },
                    ) {
                        Text(text = "Select video")
                    }
                    Button(enabled = state.canConvert, onClick = model::convert) {
                        Text(text = "Start")
                    }
                    Button(enabled = state.converting, onClick = model::cancel) {
                        Text(text = "Cancel")
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
                    state.selectedVideo?.isHdr == false -> {
                        Text(
                            text = "SDR video selected!",
                            color = MaterialTheme.colorScheme.error,
                        )
                        Text(text = "Please select an HDR video")
                    }
                    state.canConvert && state.converting.not() -> {
                        if (state.selectedVideo?.isHdr == true) {
                            Text(text = "HDR video selected")
                            Text(text = "Ready to Convert!")
                        }
                    }
                    state.converting -> {
                        Text(text = "Converting...")
                        LinearProgressIndicator()
                    }
                }

                val result = state.convertResult
                if (result != null) {
                    when (result) {
                        is TransformerKt.Status.Failure -> {
                            Text(text = "Unable to convert to SDR!")
                            Text(
                                text = result.cause.message ?: "Unknown error",
                                color = MaterialTheme.colorScheme.error,
                            )
                        }
                        is TransformerKt.Status.Progress -> {
                            Text(text = "Progress: ${result.progress}")
                            LinearProgressIndicator(progress = result.progress / 100f)
                        }
                        is TransformerKt.Status.Success -> {
                            Text(text = "Success!")
                        }
                    }
                }

                if (!state.converting && result != null && result is TransformerKt.Status.Success) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "Before - HDR")
                        VideoPlayer(uri = state.selectedVideo!!.uri, modifier = Modifier.height(200.dp))

                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "After - SDR")
                        VideoPlayer(file = result.output, modifier = Modifier.height(200.dp))
                    }
                }
            } else {
                Text(text = "Your device does not support HDR")
            }
        }
    }
}
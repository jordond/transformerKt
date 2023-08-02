@file:OptIn(ExperimentalMaterial3Api::class)

package dev.transformerkt.demo.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.ramcosta.composedestinations.navigation.popUpTo
import com.ramcosta.composedestinations.spec.Direction
import dev.transformerkt.demo.ui.destinations.ConcatScreenDestination
import dev.transformerkt.demo.ui.destinations.HdrToSdrScreenDestination
import dev.transformerkt.demo.ui.destinations.HomeScreenDestination
import dev.transformerkt.demo.ui.destinations.TrimScreenDestination
import dev.transformerkt.demo.ui.theme.TransformerKtDemoTheme

@RootNavGraph(start = true)
@Destination
@Composable
fun HomeScreen(
    navigator: DestinationsNavigator,
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "TransformerKt Demo") })
        }
    ) { outerPadding ->
        Column(
            modifier = Modifier
                .padding(outerPadding)
                .padding(32.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Button(onClick = { navigator.nav(HdrToSdrScreenDestination) }) {
                Text(text = "Convert HDR to SDR")
            }
            Button(onClick = { navigator.nav(TrimScreenDestination) }) {
                Text(text = "Trim Video")
            }
            Button(onClick = { navigator.nav(ConcatScreenDestination) }) {
                Text(text = "Concat Video")
            }
        }
    }
}

private fun DestinationsNavigator.nav(direction: Direction) {
    navigate(direction = direction, onlyIfResumed = true) {
        popUpTo(HomeScreenDestination) {
            inclusive = true
        }
    }
}

@Preview
@Composable
fun HomeScreen_Preview() {
    TransformerKtDemoTheme {
        HomeScreen(EmptyDestinationsNavigator)
    }
}
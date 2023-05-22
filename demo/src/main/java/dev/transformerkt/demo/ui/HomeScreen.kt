package dev.transformerkt.demo.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import dev.transformerkt.demo.theme.TransformerKtDemoTheme

@Composable
fun HomeScreen() {
    Text("Hello World")
}

@Preview
@Composable
fun HomeScreenPreview() {
    TransformerKtDemoTheme {
        HomeScreen()
    }
}
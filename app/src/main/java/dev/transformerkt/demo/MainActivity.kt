package dev.transformerkt.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dev.transformerkt.demo.theme.TransformerKtDemoTheme
import dev.transformerkt.demo.ui.HomeScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TransformerKtDemoTheme {
                HomeScreen()
            }
        }
    }
}
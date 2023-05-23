package dev.transformerkt.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import dev.transformerkt.demo.ui.MainApp
import dev.transformerkt.demo.ui.theme.TransformerKtDemoTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TransformerKtDemoTheme {
                MainApp(isSystemInDarkTheme())
            }
        }
    }
}
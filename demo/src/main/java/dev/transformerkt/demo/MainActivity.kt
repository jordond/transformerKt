package dev.transformerkt.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import dagger.hilt.android.AndroidEntryPoint
import dev.transformerkt.demo.ui.MainApp
import dev.transformerkt.demo.ui.theme.TransformerKtDemoTheme

@AndroidEntryPoint
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
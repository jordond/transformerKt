package dev.transformerkt.demo.ui.effects

import android.net.Uri

data class EffectSettings(
    val brightness: Float = 0f,
    val contrast: Float = 0f,
    val audioOverlay: AudioOverlay? = null,
    val overlay: Uri? = null,
)

data class AudioOverlay(
    val uri: Uri,
    val volume: Float = 1f,
)
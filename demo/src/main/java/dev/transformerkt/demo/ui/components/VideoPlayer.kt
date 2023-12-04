package dev.transformerkt.demo.ui.components

import android.net.Uri
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import dev.transformerkt.demo.ui.effects.EffectSettings
import dev.transformerkt.dsl.effects.buildEffects
import dev.transformerkt.ktx.effects.brightness
import dev.transformerkt.ktx.effects.contrast
import io.github.aakira.napier.Napier
import java.io.File

@Composable
fun VideoPlayer(
    file: File,
    modifier: Modifier = Modifier,
    play: Boolean = true,
    effectsSettings: EffectSettings = EffectSettings(),
    useController: Boolean = false,
) {
    val mediaItem by remember(file) {
        derivedStateOf {
            MediaItem.fromUri(file.toUri())
        }
    }

    VideoPlayer(listOf(mediaItem), modifier, play, effectsSettings, useController)
}

@Composable
fun VideoPlayer(
    uri: Uri,
    modifier: Modifier = Modifier,
    play: Boolean = true,
    effectsSettings: EffectSettings = EffectSettings(),
    useController: Boolean = false,
) {
    val mediaItem by remember(uri) {
        derivedStateOf {
            MediaItem.fromUri(uri)
        }
    }

    VideoPlayer(listOf(mediaItem), modifier, play, effectsSettings, useController)
}

@Composable
fun UriVideoPlayer(
    uris: List<Uri>,
    modifier: Modifier = Modifier,
    play: Boolean = true,
    effectsSettings: EffectSettings = EffectSettings(),
    useController: Boolean = false,
) {
    val mediaItems = remember(uris) {
        uris.map { MediaItem.fromUri(it) }
    }

    VideoPlayer(mediaItems, modifier, play, effectsSettings, useController)
}

@Composable
fun VideoPlayer(
    mediaItems: List<MediaItem>,
    modifier: Modifier = Modifier,
    play: Boolean = true,
    effectsSettings: EffectSettings = EffectSettings(),
    useController: Boolean = false,
) {
    val context = LocalContext.current

    val exoPlayer = remember(mediaItems, effectsSettings) {
        val effects = buildEffects {
            brightness(effectsSettings.brightness)
            contrast(effectsSettings.contrast)

            // TODO: Add picture overlay
        }

        ExoPlayer.Builder(context).build().also { player ->
            mediaItems.forEach { player.addMediaItem(it) }
            player.repeatMode = ExoPlayer.REPEAT_MODE_ONE
            player.setVideoEffects(effects.videoEffects)
            player.prepare()
        }
    }

    LaunchedEffect(play) {
        if (play) {
            exoPlayer.play()
        } else {
            exoPlayer.pause()
        }
    }

    DisposableEffect(exoPlayer) {
        onDispose {
            exoPlayer.stop()
            exoPlayer.release()
        }
    }

    Column(modifier = modifier) {
        AndroidView(
            factory = {
                PlayerView(context).apply {
                    this.useController = useController
                    player = exoPlayer
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            }
        )
    }
}
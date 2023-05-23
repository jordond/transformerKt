package dev.transformerkt.demo.ui.components

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
import java.io.File

@Composable
fun VideoPlayer(
    file: File,
    modifier: Modifier = Modifier,
    play: Boolean = true,
) {
    val context = LocalContext.current

    val mediaItem by remember(file) {
        derivedStateOf {
            MediaItem.Builder()
                .setUri(file.toUri())
                .build()
        }
    }

    val exoPlayer by remember(mediaItem) {
        derivedStateOf {
            ExoPlayer.Builder(context).build().also { player ->
                player.addMediaItem(mediaItem)
                player.repeatMode = ExoPlayer.REPEAT_MODE_ONE
                player.prepare()
            }
        }
    }

    LaunchedEffect(play) {
        if (play) {
            exoPlayer.play()
        } else {
            exoPlayer.pause()
        }
    }

    Column(modifier = modifier) {
        DisposableEffect(
            AndroidView(
                factory = {
                    PlayerView(context).apply {
                        useController = false
                        player = exoPlayer
                        layoutParams = FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    }
                }
            )
        ) {
            onDispose {
                exoPlayer.stop()
                exoPlayer.release()
            }
        }
    }
}
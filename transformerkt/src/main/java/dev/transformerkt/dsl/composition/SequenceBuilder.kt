package dev.transformerkt.dsl.composition

import android.net.Uri
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.transformer.EditedMediaItem
import dev.transformerkt.ktx.edited
import java.io.File

@CompositionDsl
public interface SequenceBuilder {

    public fun item(
        uri: Uri,
        block: EditedMediaItem.Builder.() -> Unit = {},
    ): EditedMediaItem

    public fun item(
        file: File,
        block: EditedMediaItem.Builder.() -> Unit = {},
    ): EditedMediaItem = item(file.toUri(), block)

    public fun item(
        path: String,
        block: EditedMediaItem.Builder.() -> Unit = {},
    ): EditedMediaItem = item(File(path), block)

    public fun image(
        uri: Uri,
        durationMs: Long,
        frameRate: Int = 30,
        block: EditedMediaItem.Builder.() -> Unit = {},
    ): EditedMediaItem = item(uri) {
        setDurationUs(durationMs * 1000)
        setFrameRate(frameRate)
        apply(block)
    }

    public fun image(
        file: File,
        durationMs: Long,
        frameRate: Int = 30,
        block: EditedMediaItem.Builder.() -> Unit = {},
    ): EditedMediaItem = image(file.toUri(), durationMs, frameRate, block)

    public fun image(
        path: String,
        durationMs: Long,
        frameRate: Int = 30,
        block: EditedMediaItem.Builder.() -> Unit = {},
    ): EditedMediaItem = image(File(path), durationMs, frameRate, block)
}

internal class DefaultSequenceBuilder : SequenceBuilder {

    override fun item(uri: Uri, block: EditedMediaItem.Builder.() -> Unit): EditedMediaItem {
        return MediaItem.fromUri(uri).edited(block)
    }
}
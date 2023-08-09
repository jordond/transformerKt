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
}

internal class DefaultSequenceBuilder : SequenceBuilder {

    override fun item(uri: Uri, block: EditedMediaItem.Builder.() -> Unit): EditedMediaItem {
        return MediaItem.fromUri(uri).edited(block)
    }
}
package dev.transformerkt.ktx

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.transformer.EditedMediaItem
import dev.transformerkt.TransformerKt
import java.io.File

/**
 * Create a [TransformerKt.Input] from this [MediaItem].
 */
public fun MediaItem.asTransformerInput(): TransformerKt.Input {
    return TransformerKt.Input.MediaItem(this)
}

/**
 * Create a [TransformerKt.Input] from this [EditedMediaItem].
 */
public fun EditedMediaItem.asTransformerInput(): TransformerKt.Input {
    return TransformerKt.Input.EditedMediaItem(this)
}

/**
 * Create a [TransformerKt.Input] from this [File].
 */
public fun File.asTransformerInput(): TransformerKt.Input {
    return TransformerKt.Input.File(this)
}

/**
 * Create a [TransformerKt.Input] from this [Uri].
 */
public fun Uri.asTransformerInput(): TransformerKt.Input {
    return TransformerKt.Input.Uri(this)
}
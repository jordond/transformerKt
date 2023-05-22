package dev.transformerkt.ktx

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.transformer.EditedMediaItem
import dev.transformerkt.TransformerExecutor
import java.io.File

/**
 * Create a [TransformerExecutor.Input] from this [MediaItem].
 */
public fun MediaItem.asTransformerInput(): TransformerExecutor.Input {
    return TransformerExecutor.Input.MediaItem(this)
}

/**
 * Create a [TransformerExecutor.Input] from this [EditedMediaItem].
 */
public fun EditedMediaItem.asTransformerInput(): TransformerExecutor.Input {
    return TransformerExecutor.Input.EditedMediaItem(this)
}

/**
 * Create a [TransformerExecutor.Input] from this [File].
 */
public fun File.asTransformerInput(): TransformerExecutor.Input {
    return TransformerExecutor.Input.File(this)
}

/**
 * Create a [TransformerExecutor.Input] from this [Uri].
 */
public fun Uri.asTransformerInput(): TransformerExecutor.Input {
    return TransformerExecutor.Input.Uri(this)
}
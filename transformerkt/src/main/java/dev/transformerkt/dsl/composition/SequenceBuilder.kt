package dev.transformerkt.dsl.composition

import android.net.Uri
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.transformer.EditedMediaItem
import dev.transformerkt.dsl.effects.EffectsBuilder
import dev.transformerkt.dsl.effects.setEffects
import dev.transformerkt.ktx.edited
import java.io.File

@Suppress("MemberVisibilityCanBePrivate")
@CompositionDsl
public class SequenceBuilder {

    private val _items = mutableListOf<EditedMediaItem>()
    internal val items: List<EditedMediaItem>
        get() = _items.toList()

    public fun item(
        item: Uri,
        configure: MediaItem.Builder.() -> Unit = {},
        block: EditedMediaItem.Builder.() -> Unit = {},
    ): SequenceBuilder = apply {
        val editedItem = MediaItem.fromUri(item).buildUpon().apply(configure).build().edited(block)
        _items.add(editedItem)
    }

    public fun item(
        item: File,
        configure: MediaItem.Builder.() -> Unit = {},
        block: EditedMediaItem.Builder.() -> Unit = {},
    ): SequenceBuilder = item(item.toUri(), configure, block)

    public fun item(
        mediaItem: MediaItem,
        block: EditedMediaItem.Builder.() -> Unit = {},
    ): SequenceBuilder = apply {
        _items += mediaItem.edited(block)
    }

    public fun <T> items(
        items: List<T>,
        selector: (T) -> Uri,
        configure: MediaItem.Builder.(T) -> Unit = {},
        block: EditedMediaItem.Builder.(T) -> Unit = {},
    ): SequenceBuilder = apply {
        _items += items.map { item ->
            val uri = selector(item)
            MediaItem.fromUri(uri).buildUpon()
                .apply { configure(item) }
                .edited { block(item) }
        }
    }

    public fun items(
        uris: List<Uri>,
        configure: MediaItem.Builder.(Uri) -> Unit = {},
        block: EditedMediaItem.Builder.(Uri) -> Unit = {},
    ): SequenceBuilder = items(uris, { it }, configure, block)

    public fun files(
        files: List<File>,
        configure: MediaItem.Builder.(File) -> Unit = {},
        block: EditedMediaItem.Builder.(File) -> Unit = {},
    ): SequenceBuilder = items(files, { it.toUri() }, configure, block)

    public fun mediaItems(
        mediaItems: List<MediaItem>,
        block: EditedMediaItem.Builder.(MediaItem) -> Unit = {},
    ): SequenceBuilder = apply {
        _items += mediaItems.map { it.edited { block(it) } }
    }

    public fun image(
        uri: Uri,
        durationMs: Long,
        frameRate: Int = 30,
        block: EffectsBuilder.() -> Unit = {},
    ): SequenceBuilder = item(uri) {
        setDurationUs(durationMs * 1000)
        setFrameRate(frameRate)
        setEffects(block)
    }

    public fun image(
        file: File,
        durationMs: Long,
        frameRate: Int = 30,
        block: EffectsBuilder.() -> Unit = {},
    ): SequenceBuilder = image(file.toUri(), durationMs, frameRate, block)
}
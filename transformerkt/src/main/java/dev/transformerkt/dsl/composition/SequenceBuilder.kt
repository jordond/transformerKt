package dev.transformerkt.dsl.composition

import android.net.Uri
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.transformer.EditedMediaItem
import dev.transformerkt.dsl.effects.EffectsBuilder
import dev.transformerkt.dsl.effects.setEffects
import dev.transformerkt.ktx.edited
import java.io.File

/**
 * A DSL builder for creating a [EditedMediaItem] sequence.
 */
@Suppress("MemberVisibilityCanBePrivate")
@CompositionDsl
public class SequenceBuilder {

    private val _items = mutableListOf<EditedMediaItem>()
    internal val items: List<EditedMediaItem>
        get() = _items.toList()

    /**
     * Add a single item to the sequence from a [Uri].
     *
     * @param[item] The [Uri] of the item to add.
     * @param[configure] A lambda to configure the [MediaItem.Builder] before editing.
     * @param[block] A lambda to configure the [EditedMediaItem.Builder].
     * @return The [SequenceBuilder] instance.
     */
    public fun item(
        item: Uri,
        configure: MediaItem.Builder.() -> Unit = {},
        block: EditedMediaItem.Builder.() -> Unit = {},
    ): SequenceBuilder = apply {
        val editedItem = MediaItem.fromUri(item).buildUpon().apply(configure).build().edited(block)
        _items.add(editedItem)
    }

    /**
     * Add a single item to the sequence from a [File].
     *
     * @param[item] The [File] of the item to add.
     * @param[configure] A lambda to configure the [MediaItem.Builder] before editing.
     * @param[block] A lambda to configure the [EditedMediaItem.Builder].
     * @return The [SequenceBuilder] instance.
     */
    public fun item(
        item: File,
        configure: MediaItem.Builder.() -> Unit = {},
        block: EditedMediaItem.Builder.() -> Unit = {},
    ): SequenceBuilder = item(item.toUri(), configure, block)

    /**
     * Add a single item to the sequence from a [MediaItem].
     *
     * @param[mediaItem] The [MediaItem] to add.
     * @param[block] A lambda to configure the [EditedMediaItem.Builder].
     * @return The [SequenceBuilder] instance.
     */
    public fun item(
        mediaItem: MediaItem,
        block: EditedMediaItem.Builder.() -> Unit = {},
    ): SequenceBuilder = apply {
        _items += mediaItem.edited(block)
    }

    /**
     * Create a list of [EditedMediaItem] from a list of [items] of type [T] and add to sequence.
     *
     * @param[items] The list of items to add.
     * @param[selector] A lambda to select the [Uri] from each item.
     * @param[configure] A lambda to configure the [MediaItem.Builder] before editing.
     * @param[block] A lambda to configure the [EditedMediaItem.Builder].
     * @return The [SequenceBuilder] instance.
     */
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

    /**
     * Create a list of [EditedMediaItem] from a list of [Uri]s and add to sequence.
     *
     * @param[uris] The list of [Uri]s to add.
     * @param[configure] A lambda to configure the [MediaItem.Builder] before editing.
     * @param[block] A lambda to configure the [EditedMediaItem.Builder].
     * @return The [SequenceBuilder] instance.
     */
    public fun items(
        uris: List<Uri>,
        configure: MediaItem.Builder.(Uri) -> Unit = {},
        block: EditedMediaItem.Builder.(Uri) -> Unit = {},
    ): SequenceBuilder = items(uris, { it }, configure, block)

    /**
     * Create a list of [EditedMediaItem] from a list of [File]s and add to sequence.
     *
     * @param[files] The list of [File]s to add.
     * @param[configure] A lambda to configure the [MediaItem.Builder] before editing.
     * @param[block] A lambda to configure the [EditedMediaItem.Builder].
     * @return The [SequenceBuilder] instance.
     */
    public fun files(
        files: List<File>,
        configure: MediaItem.Builder.(File) -> Unit = {},
        block: EditedMediaItem.Builder.(File) -> Unit = {},
    ): SequenceBuilder = items(files, { it.toUri() }, configure, block)

    /**
     * Create a list of [EditedMediaItem] from a list of [MediaItem]s and add to sequence.
     *
     * @param[mediaItems] The list of [MediaItem]s to add.
     * @param[block] A lambda to configure the [EditedMediaItem.Builder].
     * @return The [SequenceBuilder] instance.
     */
    public fun mediaItems(
        mediaItems: List<MediaItem>,
        block: EditedMediaItem.Builder.(MediaItem) -> Unit = {},
    ): SequenceBuilder = apply {
        _items += mediaItems.map { it.edited { block(it) } }
    }

    /**
     * Convenience function for adding an image to the sequence.
     *
     * The image will be converted to a video with the specified [durationMs] and [frameRate].
     *
     * @param[uri] The [Uri] of the image to add.
     * @param[durationMs] The duration of the image in milliseconds.
     * @param[frameRate] The frame rate of the image.
     * @param[block] A lambda to configure the [EffectsBuilder].
     * @return The [SequenceBuilder] instance.
     */
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

    /**
     * Convenience function for adding an image to the sequence.
     *
     * The image will be converted to a video with the specified [durationMs] and [frameRate].
     *
     * @param[file] The [File] of the image to add.
     * @param[durationMs] The duration of the image in milliseconds.
     * @param[frameRate] The frame rate of the image.
     * @param[block] A lambda to configure the [EffectsBuilder].
     * @return The [SequenceBuilder] instance.
     */
    public fun image(
        file: File,
        durationMs: Long,
        frameRate: Int = 30,
        block: EffectsBuilder.() -> Unit = {},
    ): SequenceBuilder = image(file.toUri(), durationMs, frameRate, block)
}
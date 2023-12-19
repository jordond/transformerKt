package dev.transformerkt.dsl.composition

import android.net.Uri
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.effect.VideoCompositorSettings
import androidx.media3.transformer.Composition
import androidx.media3.transformer.Composition.HdrMode
import androidx.media3.transformer.EditedMediaItem
import androidx.media3.transformer.EditedMediaItemSequence
import androidx.media3.transformer.Effects
import dev.transformerkt.dsl.effects.EffectsBuilder
import dev.transformerkt.dsl.effects.buildEffects
import dev.transformerkt.ktx.edited
import dev.transformerkt.ktx.toSequence
import java.io.File

@CompositionDsl
public interface CompositionBuilder {

    /**
     * Sets whether the output file should always contain an audio track.
     *
     * The default value is false.
     *
     * - If true, the output will always contain an audio track.
     * - If false:
     *      - If the Composition export doesn't produce any audio at timestamp 0, but produces audio
     *        later on, the export is aborted.
     *      - If the Composition doesn't produce any audio during the entire export, the output won't
     *        contain any audio.
     *      - If the Composition export produces audio at timestamp 0, the output will contain an
     *        audio track.
     *
     * If the output contains an audio track, silent audio will be generated for the segments
     * where the Composition export doesn't produce any audio.
     *
     * The MIME type of the output's audio track can be set
     * using `Transformer.Builder.setAudioMimeType(String)`. The sample rate and channel count can
     * be set by passing relevant AudioProcessor instances to the Composition.
     *
     * Forcing an audio track and requesting audio transmuxing are not allowed together because
     * generating silence requires transcoding.
     *
     * **This is experimental and may be removed or changed without warning.**
     */
    public var forceAudioTrack: Boolean

    /**
     * Sets whether to transmux the media items' audio tracks.
     *
     * The default value is false.
     *
     * If the Composition contains one MediaItem, the value set is ignored. The audio track
     * will only be transcoded if necessary.
     *
     * If the input Composition contains multiple media items, all the audio tracks are transcoded
     * by default. They are all transmuxed if [transmuxAudio] is true. Transmuxed tracks must
     * be compatible (typically, all the MediaItem instances containing the track to transmux are
     * concatenated in a single [EditedMediaItemSequence] and have the same sample format
     * for that track).
     *
     * Requesting audio transmuxing and forcing an audio track are not allowed together
     * because generating silence requires transcoding.
     */
    public var transmuxAudio: Boolean

    /**
     * Sets whether to transmux the media items' video tracks.
     *
     * The default value is false.
     *
     * If the [Composition] contains one [MediaItem], the value set is ignored. The video track
     * will only be transcoded if necessary.
     *
     * If the input [Composition] contains multiple media items, all the video tracks are
     * transcoded by default. They are all transmuxed if transmuxVideo is `true`.
     * Transmuxed tracks must be compatible (typically, all the MediaItem instances containing
     * the track to transmux are concatenated in a single EditedMediaItemSequence and have the
     * same sample format for that track).
     */
    public var transmuxVideo: Boolean

    /**
     * Sets the [Composition.HdrMode] for HDR video input.
     *
     * The default value is [Composition.HDR_MODE_KEEP_HDR]. Apps that need to tone-map HDR to SDR
     * should generally prefer [Composition.HDR_MODE_TONE_MAP_HDR_TO_SDR_USING_OPEN_GL] over
     * [Composition.HDR_MODE_TONE_MAP_HDR_TO_SDR_USING_MEDIACODEC], because its behavior is likely
     * to be more consistent across devices.
     */
    public var hdrMode: @HdrMode Int

    /**
     * Sets the VideoCompositorSettings to apply to the Composition.
     *
     * The default value is [VideoCompositorSettings.DEFAULT].
     */
    public var videoCompositorSettings: VideoCompositorSettings

    /**
     * Build a [EditedMediaItemSequence] from a [block] and add it to the [Composition]
     *
     * @param[isLooping] Whether the sequence should loop.
     * @param[block] A block to configure and build the [EditedMediaItemSequence].
     */
    public fun sequenceOf(
        isLooping: Boolean = false,
        block: SequenceBuilder.() -> Unit,
    ): CompositionBuilder

    /**
     * Adds a [EditedMediaItemSequence] to the [Composition].
     *
     * @param[sequence] The [EditedMediaItemSequence] to add.
     */
    public fun add(sequence: EditedMediaItemSequence): CompositionBuilder

    /**
     * Add a single item [EditedMediaItemSequence] to the [Composition].
     *
     * @param[uri] The [Uri] of the item to add.
     * @param[isLooping] Whether the item should loop.
     * @param[configure] A block to configure and build the [MediaItem].
     * @param[block] A block to configure and build the [EditedMediaItem].
     */
    public fun add(
        uri: Uri,
        isLooping: Boolean = false,
        configure: MediaItem.Builder.() -> Unit = {},
        block: EditedMediaItem.Builder.() -> Unit = {},
    ): CompositionBuilder = add(
        mediaItem = MediaItem.Builder().setUri(uri).apply(configure).build(),
        isLooping = isLooping,
        block = block,
    )

    /**
     * Add a single item [EditedMediaItemSequence] to the [Composition].
     *
     * @param[file] The [File] of the item to add.
     * @param[isLooping] Whether the item should loop.
     * @param[configure] A block to configure and build the [MediaItem].
     * @param[block] A block to configure and build the [EditedMediaItem].
     */
    public fun add(
        file: File,
        isLooping: Boolean = false,
        configure: MediaItem.Builder.() -> Unit = {},
        block: EditedMediaItem.Builder.() -> Unit = {},
    ): CompositionBuilder = add(file.toUri(), isLooping, configure, block)

    /**
     * Add a single item [EditedMediaItemSequence] to the [Composition].
     *
     * @param[mediaItem] The [MediaItem] to add.
     * @param[isLooping] Whether the item should loop.
     * @param[block] A block to configure and build the [EditedMediaItem].
     */
    public fun add(
        mediaItem: MediaItem,
        isLooping: Boolean = false,
        block: EditedMediaItem.Builder.() -> Unit = {},
    ): CompositionBuilder = add(mediaItem.edited(block), isLooping)

    /**
     * Add a single item [EditedMediaItemSequence] to the [Composition].
     *
     * @param[editedMediaItem] The [EditedMediaItem] to add.
     * @param[isLooping] Whether the item should loop.
     */
    public fun add(
        editedMediaItem: EditedMediaItem,
        isLooping: Boolean = false,
    ): CompositionBuilder

    /**
     * Add [Effects] to the entire [Composition].
     *
     * @param[block] A block to configure and build the [Effects].
     */
    @Deprecated(
        message = "This is confusing when trying to add effects to an EditedMediaItem",
        replaceWith = ReplaceWith("setEffects(block)"),
    )
    public fun effects(block: EffectsBuilder.() -> Unit): CompositionBuilder

    /**
     * Add [Effects] to the entire [Composition].
     *
     * @param[block] A block to configure and build the [Effects].
     */
    public fun setEffects(block: EffectsBuilder.() -> Unit): CompositionBuilder

    /**
     * Build the [Composition].
     *
     * @return The [Composition] built.
     */
    public fun build(): Composition
}

internal class DefaultCompositionBuilder : CompositionBuilder {

    private val sequences: MutableList<EditedMediaItemSequence> = mutableListOf()
    private var effects: Effects = Effects.EMPTY

    override var forceAudioTrack: Boolean = false
    override var transmuxAudio: Boolean = false
    override var transmuxVideo: Boolean = false
    override var hdrMode: Int = Composition.HDR_MODE_KEEP_HDR
    override var videoCompositorSettings: VideoCompositorSettings = VideoCompositorSettings.DEFAULT

    override fun add(sequence: EditedMediaItemSequence): CompositionBuilder = apply {
        sequences += sequence
    }

    override fun add(
        editedMediaItem: EditedMediaItem,
        isLooping: Boolean,
    ): CompositionBuilder = add(editedMediaItem.toSequence(isLooping))

    override fun sequenceOf(
        isLooping: Boolean,
        block: SequenceBuilder.() -> Unit,
    ): CompositionBuilder = apply {
        sequences += SequenceBuilder().apply(block).items.toSequence(isLooping)
    }

    @Deprecated(
        message = "This is confusing when trying to add effects to an EditedMediaItem",
        replaceWith = ReplaceWith("setEffects(block)"),
    )
    override fun effects(block: EffectsBuilder.() -> Unit): CompositionBuilder = apply {
        setEffects(block)
    }

    override fun setEffects(block: EffectsBuilder.() -> Unit): CompositionBuilder = apply {
        val newEffects = buildEffects(block)
        effects = Effects(
            effects.audioProcessors + newEffects.audioProcessors,
            effects.videoEffects + newEffects.videoEffects,
        )
    }

    override fun build(): Composition = Composition.Builder(sequences)
        .setEffects(effects)
        .experimentalSetForceAudioTrack(forceAudioTrack)
        .setTransmuxAudio(transmuxAudio)
        .setTransmuxVideo(transmuxVideo)
        .setHdrMode(hdrMode)
        .setVideoCompositorSettings(videoCompositorSettings)
        .build()
}

public fun compositionOf(block: CompositionBuilder.() -> Unit): Composition {
    return DefaultCompositionBuilder().apply(block).build()
}

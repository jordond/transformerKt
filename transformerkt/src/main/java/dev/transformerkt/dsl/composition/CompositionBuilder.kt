package dev.transformerkt.dsl.composition

import androidx.media3.transformer.Composition
import androidx.media3.transformer.Composition.HdrMode
import androidx.media3.transformer.EditedMediaItem
import androidx.media3.transformer.EditedMediaItemSequence
import androidx.media3.transformer.Effects
import dev.transformerkt.dsl.effects.EffectsBuilder
import dev.transformerkt.dsl.effects.buildEffects
import dev.transformerkt.ktx.toSequence

@CompositionDsl
public interface CompositionBuilder {

    public var forceAudioTrack: Boolean
    public var transmuxAudio: Boolean
    public var transmuxVideo: Boolean
    public var hdrMode: @HdrMode Int

    public fun add(sequence: EditedMediaItemSequence): CompositionBuilder

    public fun <T> sequenceOf(
        items: List<T>,
        isLooping: Boolean = false,
        block: SequenceBuilder.(T) -> EditedMediaItem,
    ): CompositionBuilder

    public fun sequenceOf(
        isLooping: Boolean = false,
        block: SequenceBuilder.() -> List<EditedMediaItem>,
    ): CompositionBuilder

    public fun add(
        isLooping: Boolean = false,
        item: EditedMediaItem,
    ): CompositionBuilder = add(item.toSequence(isLooping))

    public fun add(
        isLooping: Boolean = false,
        block: SequenceBuilder.() -> EditedMediaItem,
    ): CompositionBuilder

    public fun effects(block: EffectsBuilder.() -> Unit): CompositionBuilder

    public fun build(): Composition
}

internal class DefaultCompositionBuilder : CompositionBuilder {

    private val sequenceBuilder = DefaultSequenceBuilder()
    private val sequences: MutableList<EditedMediaItemSequence> = mutableListOf()
    private var effects: Effects = Effects.EMPTY

    override var forceAudioTrack: Boolean = false
    override var transmuxAudio: Boolean = false
    override var transmuxVideo: Boolean = false
    override var hdrMode: Int = Composition.HDR_MODE_KEEP_HDR

    override fun add(sequence: EditedMediaItemSequence): CompositionBuilder = apply {
        sequences += sequence
    }

    override fun add(
        isLooping: Boolean,
        block: SequenceBuilder.() -> EditedMediaItem,
    ): CompositionBuilder = add(block(sequenceBuilder).toSequence(isLooping))

    override fun <T> sequenceOf(
        items: List<T>,
        isLooping: Boolean,
        block: SequenceBuilder.(T) -> EditedMediaItem,
    ): CompositionBuilder = apply {
        sequences += items.map { block(sequenceBuilder, it) }.toSequence(isLooping)
    }

    override fun sequenceOf(
        isLooping: Boolean,
        block: SequenceBuilder.() -> List<EditedMediaItem>,
    ): CompositionBuilder = apply {
        sequences += block(sequenceBuilder).toSequence(isLooping)
    }

    override fun effects(block: EffectsBuilder.() -> Unit): CompositionBuilder = apply {
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
        .build()
}

public fun compositionOf(block: CompositionBuilder.() -> Unit): Composition {
    return DefaultCompositionBuilder().apply(block).build()
}

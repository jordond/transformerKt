package dev.transformerkt.ktx

import androidx.media3.transformer.Composition
import androidx.media3.transformer.EditedMediaItemSequence

public fun composition(
    sequence: EditedMediaItemSequence,
    block: Composition.Builder.() -> Unit,
): Composition = composition(listOf(sequence), block)

public fun composition(
    vararg sequences: EditedMediaItemSequence?,
    block: Composition.Builder.() -> Unit,
): Composition = composition(sequences.toList().filterNotNull(), block)

public fun composition(
    sequences: List<EditedMediaItemSequence>,
    block: Composition.Builder.() -> Unit,
): Composition = Composition.Builder(sequences).apply(block).build()

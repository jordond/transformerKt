package dev.transformerkt.internal

import androidx.media3.transformer.TransformationRequest
import androidx.media3.transformer.Transformer
import dev.transformerkt.TransformerInput
import dev.transformerkt.TransformerKt
import dev.transformerkt.TransformerStatus
import dev.transformerkt.ktx.start
import kotlinx.coroutines.flow.Flow
import java.io.File

internal class InternalTransformerKt(
    private val transformer: Transformer,
    private val progressPollDelayMs: Long,
) : TransformerKt {

    /**
     * Wrapper around [Transformer.createTransformerCallbackFlow]
     *
     * @see TransformerKt.start
     */
    override fun start(
        input: TransformerInput,
        output: File,
        request: TransformationRequest,
    ): Flow<TransformerStatus> = transformer.createTransformerCallbackFlow(
        input = input,
        output = output,
        request = request,
        progressPollDelayMs = progressPollDelayMs,
    )

    /**
     * Wrapper around [Transformer.start].
     *
     * @see TransformerKt.start
     */
    override suspend fun start(
        input: TransformerInput,
        output: File,
        request: TransformationRequest,
        onProgress: (Int) -> Unit,
    ): TransformerStatus.Finished = transformer.start(
        input = input,
        output = output,
        request = request,
        progressPollDelayMs = progressPollDelayMs,
        onProgress = onProgress,
    )
}
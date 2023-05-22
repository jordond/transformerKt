package dev.transformerkt.internal

import androidx.media3.transformer.TransformationRequest
import androidx.media3.transformer.Transformer
import dev.transformerkt.TransformerKt
import dev.transformerkt.TransformerKt.Status
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
     * @see TransformerKt.executeFlow
     */
    override fun executeFlow(
        input: TransformerKt.Input,
        output: File,
        request: TransformationRequest,
    ): Flow<Status> = transformer.createTransformerCallbackFlow(
        input = input,
        output = output,
        request = request,
        progressPollDelayMs = progressPollDelayMs,
    )

    /**
     * Wrapper around [Transformer.start].
     *
     * @see TransformerKt.execute
     */
    override suspend fun execute(
        input: TransformerKt.Input,
        output: File,
        request: TransformationRequest,
        onProgress: (Int) -> Unit,
    ): Status.Finished = transformer.start(
        input = input,
        output = output,
        request = request,
        progressPollDelayMs = progressPollDelayMs,
        onProgress = onProgress,
    )
}
package dev.transformerkt

import androidx.annotation.IntRange
import androidx.media3.transformer.ExportResult
import androidx.media3.transformer.TransformationRequest
import androidx.media3.transformer.Transformer
import dev.drewhamilton.poko.Poko
import java.io.File

/**
 * Defines the possible states of a [TransformationRequest] execution.
 */
public sealed interface TransformerStatus {

    /**
     * Denotes the completion of a [Transformer] execution.
     */
    public sealed interface Finished : TransformerStatus

    /**
     * Current progress of a [Transformer] execution.
     *
     * @param[progress] Integer progress value between 0-100
     */
    @Poko
    public class Progress(
        @IntRange(from = 0, to = 100)
        public val progress: Int,
    ) : TransformerStatus

    /**
     * A successful [Transformer] execution.
     *
     * @param[output] The output [File] of the [Transformer] execution.
     */
    @Poko
    public class Success(
        public val output: File,
        public val exportResult: ExportResult,
    ) : TransformerStatus, Finished

    /**
     * [Transformer] encountered a failure.
     *
     * @param[cause] The [Throwable] that caused the failure.
     */
    @Poko
    public class Failure(public val cause: Throwable) : TransformerStatus, Finished
}
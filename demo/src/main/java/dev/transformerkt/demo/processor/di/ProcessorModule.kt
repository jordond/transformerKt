package dev.transformerkt.demo.processor.di

import android.content.Context
import android.os.Build
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.transformerkt.demo.processor.MediaExtractorVideoProcessor
import dev.transformerkt.demo.processor.MediaRetrieverVideoProcessor
import dev.transformerkt.demo.processor.VideoProcessor

@Module
@InstallIn(ActivityRetainedComponent::class)
class ProcessorModule {

    @Provides
    fun provideVideoProcessor(
        @ApplicationContext context: Context,
    ): VideoProcessor {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            MediaRetrieverVideoProcessor(context)
        } else {
            MediaExtractorVideoProcessor(context)
        }
    }
}
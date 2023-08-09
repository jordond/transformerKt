package dev.transformerkt.ktx.effects.speed

import android.content.Context
import android.opengl.GLES20
import androidx.media3.common.GlObjectsProvider
import androidx.media3.common.GlTextureInfo
import androidx.media3.common.VideoFrameProcessingException
import androidx.media3.common.util.GlProgram
import androidx.media3.common.util.GlUtil
import androidx.media3.common.util.Size
import androidx.media3.effect.SingleFrameGlShaderProgram
import kotlin.math.roundToLong

public class SpeedChangeShaderProgram(
    context: Context,
    useHdr: Boolean,
    private val speed: Float,
) : SingleFrameGlShaderProgram(useHdr) {

    private val VERTEX_SHADER = "shaders/vertex_shader_transformation_es2.glsl"
    private val FRAGMENT_SHADER = "shaders/fragment_shader_copy_es2.glsl"

    private val glProgram: GlProgram = GlProgram(context, VERTEX_SHADER, FRAGMENT_SHADER)

    override fun configure(inputWidth: Int, inputHeight: Int): Size {
        return Size(inputWidth, inputHeight)
    }

    override fun drawFrame(inputTexId: Int, presentationTimeUs: Long) {
        try {
            glProgram.use()
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,  /* p1 = */0,  /* p2 = */4)
        } catch (exception: GlUtil.GlException) {
            throw VideoFrameProcessingException(exception)
        }
    }

    override fun queueInputFrame(
        glObjectsProvider: GlObjectsProvider,
        inputTexture: GlTextureInfo,
        presentationTimeUs: Long,
    ) {
        val newTimeUs = (presentationTimeUs / speed).roundToLong()
        super.queueInputFrame(glObjectsProvider, inputTexture, newTimeUs)
    }

    override fun release() {
        super.release()
        try {
            glProgram.delete()
        } catch (exception: GlUtil.GlException) {
            throw VideoFrameProcessingException(exception)
        }
    }
}
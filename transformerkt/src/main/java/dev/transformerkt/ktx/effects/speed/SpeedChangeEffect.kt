package dev.transformerkt.ktx.effects.speed

import android.content.Context
import androidx.media3.effect.GlEffect
import androidx.media3.effect.GlShaderProgram

public class SpeedChangeEffect(private val speed: Float) : GlEffect {

    override fun toGlShaderProgram(context: Context, useHdr: Boolean): GlShaderProgram {
        return SpeedChangeShaderProgram(context, useHdr, speed)
    }
}
package dev.transformerkt.effects

public interface VolumeChangeProvider {

    public val initial: Float

    public fun getVolume(timeUs: Long): Float
}
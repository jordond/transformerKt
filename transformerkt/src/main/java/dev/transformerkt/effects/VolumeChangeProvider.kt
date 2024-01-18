package dev.transformerkt.effects

/**
 * Provide volume for [VolumeChangeProcessor].
 */
public interface VolumeChangeProvider {

    /**
     * The initial volume to start with.
     */
    public val initial: Float

    /**
     * Calculate the volume for the given time.
     */
    public fun getVolume(timeUs: Long): Float
}
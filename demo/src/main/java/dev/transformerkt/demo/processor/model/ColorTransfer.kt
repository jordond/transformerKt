package dev.transformerkt.demo.processor.model

import android.media.MediaFormat

sealed class ColorTransfer(val value: String) {

    object Linear : ColorTransfer("linear")
    object SDR : ColorTransfer("sdr_video")
    object St2084 : ColorTransfer("smpte2084"), LikelyHdr
    object HLG : ColorTransfer("hlg"), LikelyHdr

    companion object {

        fun from(value: Int?) = when (value) {
            MediaFormat.COLOR_TRANSFER_LINEAR -> Linear
            MediaFormat.COLOR_TRANSFER_SDR_VIDEO -> SDR
            MediaFormat.COLOR_TRANSFER_ST2084 -> St2084
            MediaFormat.COLOR_TRANSFER_HLG -> HLG
            else -> null
        }
    }
}
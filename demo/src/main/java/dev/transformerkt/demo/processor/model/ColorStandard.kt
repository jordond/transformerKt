package dev.transformerkt.demo.processor.model

import android.media.MediaFormat

sealed class ColorStandard(val value: String) {

    object BT709 : ColorStandard("bt709")
    object BT601PAL : ColorStandard("bt601_pal")
    object BT601NTSC : ColorStandard("bt601_ntsc")
    object BT2020 : ColorStandard("bt2020"), LikelyHdr

    companion object {

        fun from(value: Int?): ColorStandard? = when (value) {
            MediaFormat.COLOR_STANDARD_BT709 -> BT709
            MediaFormat.COLOR_STANDARD_BT601_PAL -> BT601PAL
            MediaFormat.COLOR_STANDARD_BT601_NTSC -> BT601NTSC
            MediaFormat.COLOR_STANDARD_BT2020 -> BT2020
            else -> null
        }
    }
}
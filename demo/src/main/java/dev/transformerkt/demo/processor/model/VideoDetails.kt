package dev.transformerkt.demo.processor.model

import android.net.Uri

data class VideoDetails(
    val uri: Uri,
    val colorStandard: ColorStandard?,
    val colorTransfer: ColorTransfer?,
    val duration: Long,
) {

    val isHdr: Boolean = colorStandard is LikelyHdr || colorTransfer is LikelyHdr
}

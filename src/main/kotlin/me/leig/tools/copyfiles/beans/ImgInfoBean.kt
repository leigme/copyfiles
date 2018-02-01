package me.leig.tools.copyfiles.beans

/**
 *
 *
 * @author leig
 * @version 20171231
 *
 */

open class ImgInfoBean(
    var imgHeight: String = "",
    var imgWidth: String = "",
    var dateTime: String = "",
    var altitude: String = "",
    var longitude: String = "",
    var latitude: String = "",
    var imgSize: Long = 0,
    var imgName: String = "",
    var imgMimeType: String = ""
)
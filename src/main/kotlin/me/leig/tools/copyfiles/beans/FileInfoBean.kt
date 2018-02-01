package me.leig.tools.copyfiles.beans

import java.util.*

/**
 *
 *
 * @author leig
 *
 */

class FileInfoBean {
    
    var id: Int = 0
    var title: String = ""
    var fileName: String = ""
    var filetype: Int = 2
    var mimetype: String = ""
    var createtime: Date = Date()
    var uploadtime: Date = Date()
    var updatetime: Date = Date()
    var deletetime: Date? = null
    var deleteflag: Int = 2
    var content: String = ""
    var filesize: Long = 0L
    var fileDir: String = ""
    var saveurl: String = ""
    var tempurl: String = ""
    var longitude: String = ""
    var latitude: String = ""
    var starttime: Date? = null
    var endtime: Date? = null
    var startnum: Int = 0
    var limitnum: Int = 20
    var pagenum: Int = 1
    var totalnum: Int = 0
    
}
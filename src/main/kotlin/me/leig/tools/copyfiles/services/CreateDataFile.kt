package me.leig.tools.copyfiles.services

import me.leig.tools.copyfiles.beans.FileInfoBean
import me.leig.tools.copyfiles.comm.Tool
import java.io.File
import java.io.FileOutputStream

/**
 *
 *
 * @author leig
 *
 */

class CreateDataFile {

    fun createSql(fileInfoBeans: MutableList<FileInfoBean>, sqlFile: File) {

        if (sqlFile.exists()) {
            sqlFile.delete()
        }

        val outFile = FileOutputStream(sqlFile)

        for (fileInfoBean in fileInfoBeans) {

            if (".DS_Store" != fileInfoBean.title) {

                val time = Tool().dateToStr(fileInfoBean.createtime).substring(0, 17) + "00"

                val sql = "INSERT INTO file " +
                        "(title, content, fileType, mimeType, fileSize, saveUrl, " +
                        "tempUrl, longitude, latitude, deleteFlag, createTime, " +
                        "uploadTime, updateTime) VALUES " +
                        "('${fileInfoBean.title}', '${fileInfoBean.content}', '${fileInfoBean.filetype}', " +
                        "'${fileInfoBean.mimetype}', ${fileInfoBean.filesize}, '${fileInfoBean.saveurl}', " +
                        "'${fileInfoBean.tempurl}', '${fileInfoBean.longitude}', '${fileInfoBean.latitude}'," +
                        "${fileInfoBean.deleteflag}, '$time', " +
                        "NOW(), " +
                        "NOW());"

                outFile.write(sql.toByteArray())
                outFile.write("\n".toByteArray())
            }
        }
        outFile.close()
    }
}
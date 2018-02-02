package me.leig.tools.copyfiles.services

import com.drew.imaging.ImageMetadataReader
import com.drew.imaging.ImageProcessingException
import com.drew.metadata.Metadata
import eu.medsea.mimeutil.MimeUtil
import me.leig.tools.copyfiles.beans.FileInfoBean
import me.leig.tools.copyfiles.beans.ImgInfoBean
import me.leig.tools.copyfiles.comm.Tool
import org.apache.log4j.Logger
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Double
import java.util.*
import javax.activation.MimetypesFileTypeMap

/**
 * 复制文件方法
 *
 * @author leig
 *
 */

class CopyFilesService constructor(private val isCopyFile: Boolean = true,
                                   private val isOverride: Boolean = true) {

    private val log = Logger.getLogger(CopyFilesService::class.java)

    val fileInfoBeans: MutableList<FileInfoBean> = mutableListOf()

    fun copyFiles(source: File, target: File) {

        if (!source.exists() || !target.isDirectory) {
            log.error("源文件不存在或者目标文件夹不存在")
            return
        }

        if (source.isDirectory) {
            val files = source.listFiles()

            if (null != files && files.isNotEmpty()) {
                for (file in files) {
                    copyFiles(file, target)
                }
            }

        } else {

            val inFile = FileInputStream(source)

            val mimeType = MimeUtil.getMimeTypes(source)

            val fileInfoBean = FileInfoBean()

            fileInfoBean.fileName = source.name
            fileInfoBean.title = source.name
            fileInfoBean.content = source.name
            fileInfoBean.filesize = source.length()
            fileInfoBean.filetype = 2
            fileInfoBean.mimetype = mimeType.toString()
//            fileInfoBean.fileDir = Tool().dateToStr(Date(), "yMd")
//                    .replace("-", "")
            fileInfoBean.fileDir = "20180201"

            val suffixs = fileInfoBean.fileName.split(".")

            val suffix = suffixs[suffixs.size - 1]

            // 图片处理
            if (mimeType.contains("image/png") || mimeType.contains("image/jpeg")) {

                val imgInfoBean = parseImgInfo(source)

                fileInfoBean.mimetype = mimeType.toString()

                if (null != imgInfoBean?.dateTime) {

                    if ("" != imgInfoBean.dateTime) {

                        try {

                            fileInfoBean.createtime = Tool().strToDate(imgInfoBean.dateTime, "yyyy:MM:dd HH:mm:SS")

                            val timeString = imgInfoBean.dateTime.replace(":", "")

                            fileInfoBean.fileDir = timeString.substring(0, 8)

                            fileInfoBean.fileName = StringBuilder(timeString.replace(" ", ""))
                                    .append(".")
                                    .append(suffix)
                                    .toString()

                        } catch (e: Exception) {

                            fileInfoBean.fileDir = "exception"

                            fileInfoBean.createtime = Date()

                        }
                    }

                    fileInfoBean.longitude = imgInfoBean.longitude

                    fileInfoBean.latitude = imgInfoBean.latitude
                }
            }

            // 存储路径
            fileInfoBean.saveurl = StringBuilder(fileInfoBean.fileDir)
                    .append(File.separatorChar)
                    .append(fileInfoBean.fileName)
                    .toString()

            if (!File(target, fileInfoBean.fileDir).exists()) {
                File(target, fileInfoBean.fileDir).mkdir()
            }

            if (isCopyFile) {

                if (File(target, fileInfoBean.saveurl).exists()) {

                    if (isOverride) {

                        log.info("正在将${fileInfoBean.saveurl} 复制到${fileInfoBean.title}")

                        val outFile = FileOutputStream(File(target, fileInfoBean.saveurl))

                        val buffer = ByteArray(1024)

                        var length: Int = inFile.read(buffer)

                        while (length > 0) {
                            outFile.write(buffer, 0, length)
                            length = inFile.read(buffer)
                        }

                        inFile.close()
                        outFile.close()
                    }

                } else {

                    log.info("正在将${fileInfoBean.saveurl} 复制到${fileInfoBean.title}")

                    val outFile = FileOutputStream(File(target, fileInfoBean.saveurl))

                    val buffer = ByteArray(1024)

                    var length: Int = inFile.read(buffer)

                    while (length > 0) {
                        outFile.write(buffer, 0, length)
                        length = inFile.read(buffer)
                    }

                    inFile.close()
                    outFile.close()
                }
            }

            fileInfoBeans.add(fileInfoBean)
        }
    }

    /**
     * 图片信息获取metadata元数据信息
     * @param fileName 需要解析的文件
     * @return
     */
    fun parseImgInfo(file: File): ImgInfoBean? {

        var imgInfoBean: ImgInfoBean? = null

        try {
            val metadata = ImageMetadataReader.readMetadata(file)

            imgInfoBean = printImageTags(file, metadata)

        } catch (e: ImageProcessingException) {
            System.err.println("error 1a: " + e)
        } catch (e: IOException) {
            System.err.println("error 1b: " + e)
        }

        return imgInfoBean
    }

    /**
     * 读取metadata里面的信息
     * @param sourceFile 源文件
     * @param metadata metadata元数据信息
     * @return
     */
    private fun printImageTags(sourceFile: File, metadata: Metadata): ImgInfoBean {

        val imgInfoBean = ImgInfoBean()

        imgInfoBean.imgName = sourceFile.name
        imgInfoBean.imgMimeType = MimetypesFileTypeMap().getContentType(sourceFile)
        imgInfoBean.imgSize = sourceFile.totalSpace

        for (directory in metadata.directories) {
            for (tag in directory.tags) {
                val tagName = tag.tagName
                val desc = tag.description
                if (null != desc) {
                    when (tagName) {
                    // 图片高度
                        "Image Height" ->
                            imgInfoBean.imgHeight = desc
                    // 图片宽度
                        "Image Width" ->
                            imgInfoBean.imgWidth = desc
                    // 拍摄时间
                        "Date/Time Original" ->
                            imgInfoBean.dateTime = desc
                    // 海拔
                        "GPS Altitude" ->
                            imgInfoBean.altitude = desc
                    // 经度
                        "GPS Longitude" ->
                            imgInfoBean.longitude = pointToLatlong(desc)
                    // 纬度
                        "GPS Latitude" ->
                            imgInfoBean.latitude = pointToLatlong(desc)
                    }
                }
            }
            for (error in directory.errors) {
                log.error("ERROR: " + error)
            }
        }
        return imgInfoBean
    }

    /**
     * 经纬度转换  度分秒转换
     * @param point 坐标点
     * @return
     */
    fun pointToLatlong (point: String): String {
        val du = Double.parseDouble(point.substring(0, point.indexOf("°")).trim())
        val fen = Double.parseDouble(point.substring(point.indexOf("°") + 1, point.indexOf("'")).trim())
        val miao = Double.parseDouble(point.substring(point.indexOf("'") + 1, point.indexOf("\"")).trim())
        val duStr = du + fen / 60 + miao / 60 / 60
        return duStr.toString()
    }
}
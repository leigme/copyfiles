package me.leig.tools.copyfiles

import eu.medsea.mimeutil.MimeUtil
import me.leig.tools.copyfiles.comm.Tool
import me.leig.tools.copyfiles.services.CopyFilesService
import me.leig.tools.copyfiles.services.CreateDataFile
import org.apache.log4j.Logger
import java.io.File

/**
 *  入口程序
 *
 * @author leig
 *
 */
class App

val log = Logger.getLogger(App::class.java)!!

fun main(args: Array<String>) {

    log.info("app start...")

    try {
        MimeUtil.registerMimeDetector("eu.medsea.mimeutil.detector.MagicMimeMimeDetector")

        val cb = Tool().analysisConf(File("config.json"))

        val cfs = CopyFilesService(isCopyFile = cb.isCopyFile, isOverride = cb.isOverride)

        cfs.copyFiles(File(cb.sourcePath), File(cb.targetPath))

        if (cb.isCreateSQL) {

            val cdf = CreateDataFile()

            cdf.createSql(cfs.fileInfoBeans, File(cb.sqlFilePath + "image.sql"))
        }

    } catch (e: Exception) {

        e.printStackTrace()

        log.error("捕获异常: " + e.message)

    }

    log.info("app end!")
}
package me.leig.tools.copyfiles.comm

import com.google.gson.Gson
import me.leig.tools.copyfiles.beans.ConfigBean
import org.apache.log4j.Logger
import java.io.File
import java.io.FileInputStream
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 *
 *
 * @author leig
 *
 */

class Tool {

    private val log = Logger.getLogger(Tool::class.java)

    fun analysisConf(conf: File): ConfigBean {

        val stringBuilder = StringBuilder()

        val inFile = FileInputStream(conf)
        val buffer = ByteArray(1024)

        var length = inFile.read(buffer)

        while (0 < length) {
            stringBuilder.append(String(buffer, 0, length))
            length = inFile.read(buffer)
        }

        inFile.close()

        return Gson().fromJson(stringBuilder.toString(), ConfigBean::class.java)
    }

    /**
     * 生成随机字符串
     *
     * @author leig
     * @version 20171231
     *
     */
    fun randomStr(num: Int = 4): String {

        val sb = StringBuffer()

        for (i in 0 until num) {
            sb.append(Random().nextInt(10))
        }

        return sb.toString()
    }

    /**
     * 时间转字符串
     *
     * @author leig
     * @version 20171231
     *
     */
    fun dateToStr(date: Date, formatter: String = "yyyy-MM-dd HH:mm:SS"): String {
        date.time
        return when(formatter) {
            "yyyy-MM-dd HH:mm:SS" -> {
                SimpleDateFormat(formatter).format(date)
            }
            "yMd" -> {
                SimpleDateFormat("yyyy-MM-dd").format(date)
            }
            else -> {
                log.error("$formatter 字符串格式错误...")
                throw Exception("时间格式错误...")
            }
        }
    }

    /**
     * 字符串转时间
     *
     * @author leig
     * @version 20171231
     *
     */
    fun strToDate(dateStr: String, formatter: String = "yyyy-MM-dd HH:mm:SS"): Date {
        return try {
            when(dateStr.length) {
                10 -> {
                    SimpleDateFormat("yyyy-MM-dd").parse(dateStr)
                }
                19 -> {
                    SimpleDateFormat(formatter).parse(dateStr)
                }
                else -> {
                    log.error("$dateStr 字符串格式错误...")
                    throw Exception("字符串格式错误")
                }
            }
        } catch (e: ParseException) {
            log.error("捕获异常: ${e.message}")
            throw e
        }
    }

}
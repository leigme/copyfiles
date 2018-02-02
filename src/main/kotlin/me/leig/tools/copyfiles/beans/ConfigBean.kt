package me.leig.tools.copyfiles.beans

/**
 *
 *
 * @author leig
 *
 */

data class ConfigBean(
        val sourcePath: String = "",
        val targetPath: String = "",
        val sqlFilePath: String = "",
        val isCopyFile: Boolean = true,
        val isOverride: Boolean = true,
        val isCreateSQL: Boolean = true
)
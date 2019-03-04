package com.ml.lib_tool

import android.os.Environment
import java.io.File

/**
 * 路径获取帮助类
 */
class FilePathUtil private constructor(){

    companion object {
        val instance   by lazy { FilePathUtil() }
        val IMAGES_PATH = "images"
    }




    //****系统文件目录**********************************************************************************************

    /**
     * @return 程序系统文件目录
     */
    fun getFileDir(): String {
        return getApplicationContext().filesDir.toString()
    }

    /**
     * @param customPath 自定义路径
     * @return 程序系统文件目录绝对路径
     */
    fun getFileDir( customPath: String): String {
        val path = "${getApplicationContext().filesDir }${formatPath(customPath)}"
        mkdir(path)
        return path
    }


    //****系统缓存目录**********************************************************************************************

    /**
     * @return 程序系统缓存目录
     */
    fun getCacheDir(): String {
        return getApplicationContext().cacheDir.toString()
    }

    /**
     * @param customPath 自定义路径
     * @return 程序系统缓存目录
     */
    fun getCacheDir( customPath: String): String {
        val path = "${getApplicationContext().cacheDir}${formatPath(customPath)}"
        mkdir(path)
        return path
    }


    //****Sdcard文件目录**********************************************************************************************

    /**
     * @return 内存卡文件目录
     */
    fun getExternalFileDir(): String {
        return getApplicationContext().getExternalFilesDir("").toString()
    }

    /**
     * @param customPath 自定义路径
     * @return 内存卡文件目录
     */
    fun getExternalFileDir( customPath: String): String {
        val path = "${getApplicationContext().getExternalFilesDir("")}${formatPath(customPath)}"
        mkdir(path)
        return path
    }

    //****Sdcard缓存目录**********************************************************************************************

    /**
     * @return 内存卡缓存目录
     */
    fun getExternalCacheDir(): String {
        return getApplicationContext().externalCacheDir.toString()
    }

    /**
     * @param customPath 自定义路径
     * @return 内存卡缓存目录
     */
    fun getExternalCacheDir( customPath: String): String {
        val path = "${getApplicationContext().externalCacheDir}${formatPath(customPath)}"
        mkdir(path)
        return path
    }

    //****公共文件夹**********************************************************************************************

    /**
     * @return 公共下载文件夹
     */
    fun getPublicDownloadDir(): String {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath
    }


    //****相关工具**********************************************************************************************

    /**
     * 创建文件夹
     *
     * @param DirPath 文件夹路径
     */
    fun mkdir(DirPath: String) {
        val file = File(DirPath)
        if (!(file.exists() && file.isDirectory)) {
            file.mkdirs()
        }
    }

    /**
     * 格式化文件路径
     * 示例：  传入 "sloop" "/sloop" "sloop/" "/sloop/"
     * 返回 "/sloop"
     */
    private fun formatPath(path: String): String {
        var newPath = path
        if (!newPath.startsWith("/"))
            newPath = "/$newPath"

        if (newPath.endsWith("/"))
            newPath = String(newPath.toCharArray(), 0, newPath.length - 1)
        return newPath
    }

    /**
     * @return 存储卡是否挂载(存在)
     */
    fun isMountSdcard(): Boolean {
        val status = Environment.getExternalStorageState()
        return status == Environment.MEDIA_MOUNTED
    }

}
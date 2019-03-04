package com.ml.lib_tool

import java.text.SimpleDateFormat
import java.util.*
/**
 * 时间工具类
 */
class TimeUtil private constructor(){

    companion object {
        val instance by lazy { TimeUtil() }

        private val DEFAULT_DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        private val DATE_FORMAT_DATE = SimpleDateFormat("yyyy-MM-dd")

        private val minute = (60 * 1000).toLong()// 1分钟
        private val hour = 60 * minute// 1小时
        private val day = 24 * hour// 1天
        private val month = 31 * day// 月
        private val year = 12 * month// 年
    }

    /**
     * 毫秒转换日期格式
     * @param timeInMillis
     * @param dateFormat
     * @return
     */
    fun getTime(timeInMillis: Long, dateFormat: SimpleDateFormat = DEFAULT_DATE_FORMAT): String {
        return dateFormat.format(Date(timeInMillis))
    }


    /**
     * 获取当前时间的毫秒数
     * @return
     */
    fun getCurrentTimeInLong(): Long {
        return System.currentTimeMillis()
    }


    /**
     * 获取当前日期
     * @return
     */
    fun getCurrentTimeInString(dateFormat: SimpleDateFormat = DEFAULT_DATE_FORMAT): String {
        return getTime(getCurrentTimeInLong(),dateFormat)
    }


    /**
     * 获得几天之前或者几天之后的日期
     * @param diff 差值：正的往后推，负的往前推
     * @return
     */
    fun getOtherDay(diff: Int,date: Date = Date()): String {
        val mCalendar = Calendar.getInstance()
        mCalendar.time = date
        mCalendar.add(Calendar.DATE, diff)
        return getTime(mCalendar.time.time)
    }


    /**
     * 返回文字描述的日期
     *
     * @param date
     * @return
     */
    fun getTimeFormatText(timeInMillis: Long): String {

        val diff = Date().time - timeInMillis
        return when(diff){
            in year..diff ->{ "${diff / year}年前" }
            in month..diff ->{ "${diff / month}月前" }
            in day..diff ->{ "${diff / day}天前" }
            in hour..diff ->{ "${diff / hour}小时前" }
            in minute..diff ->{ "${diff / minute}分钟前" }
            else -> "刚刚"
        }
    }



}
package com.ml.lib_tool

import android.graphics.Point
import android.view.View

class DisplayUtil private constructor(){

    companion object {
        val instance   by lazy { DisplayUtil() }
    }


    fun dip2px(dipValue: Float): Int {
        val scale = getApplicationContext().resources.displayMetrics.density
        return (dipValue * scale + 0.5f).toInt()
    }

    fun px2dip(pxValue: Float): Int {
        val scale = getApplicationContext().resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }

    fun getScreenMetrics(): Point {
        val dm = getApplicationContext().resources.displayMetrics
        val w_screen = dm.widthPixels
        val h_screen = dm.heightPixels
        return Point(w_screen, h_screen)
    }

    /**
     *
     * @return
     */
    fun getScreenRate(): Float {
        val P = getScreenMetrics()
        val H = P.y.toFloat()
        val W = P.x.toFloat()
        return H / W
    }


    fun getScreenWidth(): Int {
        return getApplicationContext().resources.displayMetrics.widthPixels
    }


    fun getScreenHeight(): Int {
        return getApplicationContext().resources.displayMetrics.heightPixels
    }


    fun getScreenDensity(): Float {
        return getApplicationContext().resources.displayMetrics.density
    }

    fun pt2px(spValue: Float): Double {
        return spValue.toDouble() * 2.22
    }

    fun getViewSize(view: View): IntArray {
        val width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        val height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        view.measure(width, height)
        return intArrayOf(view.measuredWidth, view.measuredHeight)
    }

}
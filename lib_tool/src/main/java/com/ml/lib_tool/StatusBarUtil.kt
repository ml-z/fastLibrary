package com.ml.lib_tool

import android.view.ViewGroup
import android.app.Activity
import android.view.WindowManager
import android.annotation.TargetApi
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.View


class StatusBarUtil private constructor(){

    companion object {
        val instance by lazy { StatusBarUtil() }
    }


    /**
     * 设置状态栏背景
     * @param statusBarView 做为状态栏背景的view
     */
    fun setStatusBarView(activity: Activity,statusBarView :View ){
        transparentStatusBar(activity)
        val params = statusBarView.layoutParams
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height =  getStatusBarHeight(statusBarView.context)
        statusBarView.layoutParams = params
    }

    /**
     * 设置状态栏,白色字体图标
     */
    fun setStatusBarViewDarkMode(activity: Activity,statusBarView :View ){
        setStatusBarView(activity,statusBarView)
        setDarkMode(activity,statusBarView)
    }

    /**
     * 设置状态栏,黑色字体图标
     */
    fun setStatusBarViewLightMode(activity: Activity,statusBarView :View ){
        setStatusBarView(activity,statusBarView)
        setLightMode(activity,statusBarView)
    }


    /**
     * 状态栏黑色字体图标
     */
    @TargetApi(Build.VERSION_CODES.M)
    private fun setLightMode(activity: Activity,statusBarView :View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }else{
            //设置背景灰色
            statusBarView.setBackgroundColor(Color.parseColor("#bebebe"))
        }
    }

    /**
     * 状态栏白色字体图标
     */
    @TargetApi(Build.VERSION_CODES.M)
    private fun setDarkMode(activity: Activity,statusBarView :View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }else{
            //设置背景灰色
            statusBarView.setBackgroundColor(Color.parseColor("#bebebe"))
        }
    }


    /**
     * 使状态栏透明
     */
    private fun transparentStatusBar(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                activity.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                activity.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                activity.window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
                activity.window.statusBarColor = Color.TRANSPARENT
            }else{
                activity.window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)//设置透明状态栏
            }
        }
    }


    /**
     * 获取状态栏高度
     * @param context context
     * @return 状态栏高度
     */
    fun getStatusBarHeight(context: Context): Int {
        // 获得状态栏高度
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        return if(resourceId!=0) context.resources.getDimensionPixelSize(resourceId)
                else context.resources.getDimensionPixelSize(R.dimen.dp_20)
    }

    /**
     * 隐藏状态栏
     */
    fun hiddenStatusBar(activity: Activity) {
        val attrs = activity.window.attributes
        attrs.flags = attrs.flags or WindowManager.LayoutParams.FLAG_FULLSCREEN
        activity.window.attributes = attrs
    }

    /**
     * 显示状态栏
     */
    fun showStatusBar(activity: Activity) {
        val attrs = activity.window.attributes
        attrs.flags = attrs.flags and WindowManager.LayoutParams.FLAG_FULLSCREEN.inv()
        activity.window.attributes = attrs
    }

}
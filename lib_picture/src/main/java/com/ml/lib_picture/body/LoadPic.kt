package com.ml.lib_picture.body

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ml.lib_tool.getApplicationContext
import java.io.File

/**
 * 图片加载类
 */
class LoadPic private constructor(){


    companion object {
        val instance   by lazy { LoadPic() }
    }

    /**
     * 加载图片
     */
    fun load(context: Context, model:Any,placeholderId:Int,errorId:Int,imageView: ImageView){

        when (context){

            is Activity ->{
                Glide.with(context as Activity)
                        .load(model).apply(getDefaultRequestOptions(placeholderId,errorId)).into(imageView)
            }

            is Fragment ->{
                Glide.with(context as Fragment)

                        .load(model).apply(getDefaultRequestOptions(placeholderId,errorId)).into(imageView)
            }

            else ->{
                Glide.with(context)
                        .load(model).apply(getDefaultRequestOptions(placeholderId,errorId)).into(imageView)
            }

        }


    }


    /**
     * 模糊
     */
//    fun loadBlur(context: Context, string:String,placeholderId:Int,errorId:Int,round:Int,imageView: ImageView){
//
//    }

    /**
     * 获取 Drawable
     */
    fun loadDrawable(file: File): Drawable {
        return Glide.with(getApplicationContext()).load(file).submit().get()
    }

    /**
     * 获取 Drawable，指定宽高
     */
    fun loadDrawable(file: File, width:Int,  height:Int): Drawable {
        return Glide.with(getApplicationContext()).load(file).submit(width,height).get()
    }

    private fun getDefaultRequestOptions(placeholderId:Int,errorId:Int): RequestOptions {

        return RequestOptions().placeholder(placeholderId).error(errorId)

    }

}
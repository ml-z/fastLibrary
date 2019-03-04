package com.ml.lib_picture.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.ml.lib_picture.entity.AlbumImage
import com.ml.lib_picture.ui.photo.PhotoAlbumActivity
import com.ml.lib_picture.ui.photo.PhotoLookOverActivity

/**
 * 跳转到相册
 */
fun Context.skipPhotoAlbumActivity(requestCode:Int){

    val intent =  Intent(this, PhotoAlbumActivity::class.java)

    when(this){
        is Activity   ->{startActivityForResult(intent,requestCode)}
        is Fragment ->{startActivityForResult(intent,requestCode)}
        else ->{
            startActivity(Intent(this, PhotoAlbumActivity::class.java))
        }
    }
}

/**
 * 跳转到相册预览界面
 * @param currentPosition 当前位置
 * @param maxChooseNumber 可选择的最大值
 * @param picList 已有的图片地址
 * @param pageIndex 查询到第几条
 * @param chooseList 已选择的图片
 * @param requestCode 返回码
 */
fun Context.skipPhotoLookOverActivity(currentPosition:Int, maxChooseNumber:Int,picList:ArrayList<AlbumImage>,
                                      pageIndex:Int,chooseList:ArrayList<AlbumImage>, requestCode:Int){

    val intent = Intent(this, PhotoLookOverActivity::class.java).apply {
        putExtra("currentPosition",currentPosition)
        putExtra("picList",picList)
        putExtra("maxChooseNumber",maxChooseNumber)
        putExtra("chooseList",chooseList)
        putExtra("pageIndex",pageIndex)
    }

    when(this){
        is Activity   ->{startActivityForResult(intent,requestCode)}
        is Fragment ->{startActivityForResult(intent,requestCode)}
        else ->{
            startActivity(intent)
        }
    }

}


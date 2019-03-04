package com.ml.lib_picture.util

import android.annotation.SuppressLint
import android.graphics.Bitmap
import com.light.body.RxLight
import com.ml.lib_tool.FilePathUtil
import io.reactivex.*

/**
 * 图片压缩
 */
@SuppressLint("CheckResult")
fun Bitmap.mCompress(outPath:String?=null): Flowable<String> {

    val newOutPath = outPath ?: "${FilePathUtil.instance.getExternalCacheDir(FilePathUtil.IMAGES_PATH)}/${System.currentTimeMillis()}.jpg"

    return Flowable.just(this@mCompress)
            .compose(RxLight.compress(newOutPath)).map { t -> if(t) newOutPath else "" }

}
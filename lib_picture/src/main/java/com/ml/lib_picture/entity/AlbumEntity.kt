package com.ml.lib_picture.entity

import android.text.TextUtils
import java.io.Serializable

data class AlbumFile (var count:Int,var bucketId:String?, var bucketDisplayName:String,
                      var miniThumbMagic :String? ,var data:String): Serializable

data class AlbumImage (var data:String,var miniThumbMagic:String?,var size:String,
                       var dateModified:String,var bucketId:String) : Serializable {
    override fun equals(other: Any?): Boolean {
        if(other is AlbumImage){
            return TextUtils.equals(other.data,data)
        }
        return super.equals(other)
    }
}

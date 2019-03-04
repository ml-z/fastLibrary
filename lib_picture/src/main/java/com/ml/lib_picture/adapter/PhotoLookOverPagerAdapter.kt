package com.ml.lib_picture.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.github.chrisbanes.photoview.OnPhotoTapListener
import com.github.chrisbanes.photoview.PhotoView
import com.ml.lib_picture.R
import com.ml.lib_picture.body.LoadPic
import com.ml.lib_picture.entity.AlbumImage
import io.reactivex.Observable
import io.reactivex.functions.Consumer

class PhotoLookOverPagerAdapter(context: Context) : PagerAdapter() {

    val mContext = context
    val infoList:ArrayList<AlbumImage> = ArrayList()

    //点击
    var itemClickConsumer: Consumer<Int>? = null

    override fun isViewFromObject(p0: View, p1: Any): Boolean = p0==p1

    override fun getCount(): Int = infoList.size

    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        val photoView = View.inflate(mContext, R.layout.picture_photo_look_over_item,null) as PhotoView

        //点击事件
        photoView.setOnPhotoTapListener(object : OnPhotoTapListener {
            @SuppressLint("CheckResult")
            override fun onPhotoTap(view: ImageView?, x: Float, y: Float) {
                itemClickConsumer?.apply {
                    Observable.just(position).subscribe(this)
                }
            }
        })


        LoadPic.instance.load(mContext,infoList[position].data,0
                ,0,photoView)
        container.addView(photoView)

        return photoView
    }

    override fun destroyItem(container: ViewGroup, position: Int, any: Any) {
        container.removeView(any as View)
    }

}
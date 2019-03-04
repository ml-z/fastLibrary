package com.ml.lib_picture.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.ml.lib_picture.R
import com.ml.lib_picture.body.LoadPic
import com.ml.lib_picture.entity.AlbumImage
import com.ml.lib_tool.rxOnClick
import io.reactivex.Observable
import io.reactivex.functions.Consumer

/**
 * 查看大图，已选择的小图适配器
 */
class PhotoLookOverChooseAdapter(context:Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val mContext = context
    val infoList : ArrayList<AlbumImage> = ArrayList()
    //点击
    var itemClickConsumer: Consumer<Int>? = null
    /**
     * 当前选择的位置
     */
    var mCurrentPosition:Int = 0

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        return ItemViewHolder(View.inflate(mContext,R.layout.picture_photo_look_over_recyclerview_item,null))
    }

    override fun getItemCount(): Int = infoList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
        val info = infoList[p1]
        ( p0 as ItemViewHolder).apply {

            thumbnail_iv.apply {
                LoadPic.instance.load(mContext,info.data,0,0,
                        this)

                rxOnClick {

                    itemClickConsumer?.apply { Observable.just(p1).subscribe(this) }
                }
            }

            if(mCurrentPosition==p1) thumbnail_border_view.visibility = View.VISIBLE
            else thumbnail_border_view.visibility = View.GONE

        }

    }


    internal class ItemViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val thumbnail_iv = itemView.findViewById<ImageView>(R.id.thumbnail_iv)
        val thumbnail_border_view = itemView.findViewById<View>(R.id.thumbnail_border_view)
    }


}
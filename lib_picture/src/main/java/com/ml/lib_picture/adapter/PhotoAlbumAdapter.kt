package com.ml.lib_picture.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.ml.lib_picture.R
import com.ml.lib_picture.body.LoadPic
import com.ml.lib_picture.entity.AlbumImage
import com.ml.lib_tool.rxOnClick
import io.reactivex.Observable
import io.reactivex.functions.Consumer

/**
 * 相册适配器
 */
class PhotoAlbumAdapter(context:Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val mContext = context
    val infoList : ArrayList<AlbumImage> = ArrayList()
    //点击
    var itemClickConsumer: Consumer<Int>? = null
    //选择
    var itemChooseConsumer: Consumer<Int>? = null
    //加载
    var onOnLoadMoreConsumer: Consumer<Int>? = null
    //是否可以加载
    var isLoadMore = true
    var itemChooseList : ArrayList<AlbumImage> = ArrayList()


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        return ItemViewHolder(View.inflate(mContext,R.layout.picture_photo_recyclerview_item,null))
    }

    override fun getItemCount(): Int = infoList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
        val info = infoList[p1]
        ( p0 as ItemViewHolder).apply {

            thumbnail_iv.apply {
                LoadPic.instance.load(mContext,info.data,R.mipmap.picture_are,R.mipmap.picture_are,
                        this)

                rxOnClick {
                    itemClickConsumer?.apply { Observable.just(p1).subscribe(this) }
                }
            }

            thumbnail_cb.apply {

                isChecked = (-1 != itemChooseList.indexOf(info))

                setOnClickListener {
                    if(isChecked){//选中
                        itemChooseList.add(info)
                    }else{//取消选中
                        itemChooseList.remove(info)
                    }
                    itemChooseConsumer?.apply { Observable.just(p1).subscribe(this) }
                }
            }

            //预加载
            if(p1==itemCount-16&&isLoadMore)  onOnLoadMoreConsumer?.apply { Observable.just(p1).subscribe(this) }
        }

    }


    internal class ItemViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val thumbnail_iv = itemView.findViewById<ImageView>(R.id.thumbnail_iv)
        val thumbnail_cb = itemView.findViewById<CheckBox>(R.id.thumbnail_cb)
    }


}
package com.ml.lib_picture.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ml.lib_picture.R
import com.ml.lib_picture.body.LoadPic
import com.ml.lib_picture.entity.AlbumFile
import com.ml.lib_tool.rxOnClick
import com.ml.lib_tool.setTextDBC
import io.reactivex.Observable
import io.reactivex.functions.Consumer

/**
 * 目录选择适配器
 */
class DirectoryPopupWindowAdapter(context:Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val mContext = context
    val infoList : ArrayList<AlbumFile> = ArrayList()
    /**
     * 选中的目录
     */
    var selectedBucketDisplayName = mContext.resources.getString(R.string.picture_album_name_all)
    var selectedBucketId:String? = null

    var itemClickConsumer: Consumer<AlbumFile>? = null

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        return ItemViewHolder(View.inflate(mContext,R.layout.picture_photo_directory_popupwindow_item,null))
    }

    override fun getItemCount(): Int = infoList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
        val viewHolder = p0 as ItemViewHolder
        val info = infoList[p1]

        LoadPic.instance.load(mContext, info.data,0,0,
                viewHolder.directory_pic_iv)

        viewHolder.directory_name_tv.setTextDBC(info.bucketDisplayName)
        viewHolder.directory_pic_num_tv.text = "${info.count}张"

        if(info.count>1){
            viewHolder.directory_pic_bg_iv.visibility = View.VISIBLE
        }else{
            viewHolder.directory_pic_bg_iv.visibility = View.GONE
        }

        if(TextUtils.equals(selectedBucketDisplayName,info.bucketDisplayName)){
            viewHolder.directory_selected_cb.visibility = View.VISIBLE
        }else{
            viewHolder.directory_selected_cb.visibility = View.GONE
        }


        viewHolder.itemView.rxOnClick {
            selectedBucketDisplayName = info.bucketDisplayName
            selectedBucketId = info.bucketId
            notifyDataSetChanged()
            itemClickConsumer?.apply { Observable.just(info).subscribe(this) }
        }
    }


    internal class ItemViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val directory_pic_bg_iv = itemView.findViewById<ImageView>(R.id.directory_pic_bg_iv)
        val directory_pic_iv = itemView.findViewById<ImageView>(R.id.directory_pic_iv)
        val directory_name_tv = itemView.findViewById<TextView>(R.id.directory_name_tv)
        val directory_pic_num_tv = itemView.findViewById<TextView>(R.id.directory_pic_num_tv)
        val directory_selected_cb = itemView.findViewById<CheckBox>(R.id.directory_selected_cb)
    }


}
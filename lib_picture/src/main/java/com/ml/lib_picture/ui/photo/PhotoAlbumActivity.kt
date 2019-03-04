package com.ml.lib_picture.ui.photo

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import com.ml.lib_picture.R
import com.ml.lib_picture.body.AlbumLoader
import com.ml.lib_tool.applySchedulers
import com.ml.lib_tool.rxOnClick
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import kotlinx.android.synthetic.main.picture_photo_album_activity.*
import android.view.View
import com.ml.lib_picture.adapter.DirectoryPopupWindowAdapter
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import com.ml.lib_picture.adapter.PhotoAlbumAdapter
import com.ml.lib_picture.entity.AlbumImage
import com.ml.lib_picture.util.skipPhotoLookOverActivity
import com.ml.lib_tool.StatusBarUtil
import com.ml.lib_tool.TimeUtil
import com.ml.lib_tool.recyclerview.GridSpacingItemDecoration
import io.reactivex.Observable
import io.reactivex.functions.Consumer
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


/**
 * 自定义相册
 */
class PhotoAlbumActivity : RxAppCompatActivity() {
    /**
     * 开始条数
     */
    var pageIndex:Int = 0
    var maxChooseNumber:Int = 9
    /**
     * 小图适配器
     */
    lateinit var picAdapter:PhotoAlbumAdapter
    /**
     * 跳转到预览页面的请求码
     */
    val REQUESTCODE_PHOTOLOOKOVER = 1010

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.picture_photo_album_activity)

        StatusBarUtil.instance.setStatusBarView(this,status_bar_view)

        picAdapter = PhotoAlbumAdapter(this@PhotoAlbumActivity).apply {
            //点击去预览界面
            itemClickConsumer = Consumer{

                skipPhotoLookOverActivity(it,maxChooseNumber,picAdapter.infoList,pageIndex,picAdapter.itemChooseList
                        ,REQUESTCODE_PHOTOLOOKOVER)
            }
            //选中此图片
            itemChooseConsumer = Consumer{

                title_bar_right_tv.apply {
                    text = if(itemChooseList.size>0)
                                "${resources.getString(R.string.picture_complete)}(${itemChooseList.size}/$maxChooseNumber)"
                            else resources.getString(R.string.picture_complete)

                    setTextColor(if(itemChooseList.size>0) Color.parseColor("#009100") else Color.parseColor("#006000"))
                }

                look_over_tv.apply {
                    text = if(itemChooseList.size>0)
                                "${resources.getString(R.string.picture_look_over)}(${itemChooseList.size}/$maxChooseNumber)"
                            else resources.getString(R.string.picture_look_over)

                    setTextColor(if(itemChooseList.size>0) Color.parseColor("#ffffff") else Color.parseColor("#5b5b5b"))
                }
            }

            //预加载
            onOnLoadMoreConsumer = Consumer{
                pageIndex++
                loadPicturesByBucketId((directory_popupwindow_recyclerview.adapter as DirectoryPopupWindowAdapter).selectedBucketId)
            }

        }


        photo_recyclerview.apply {

            adapter = picAdapter

            layoutManager = GridLayoutManager(this@PhotoAlbumActivity,4)

            addItemDecoration(
                GridSpacingItemDecoration(
                    4,
                    resources.getDimension(R.dimen.dp_2).toInt(),
                    false
                )
            )

            addOnScrollListener(object : RecyclerView.OnScrollListener(){

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val layoutManager = photo_recyclerview.layoutManager as GridLayoutManager
                    val position = layoutManager.findFirstVisibleItemPosition()
                    photo_time_tv.text = TimeUtil.instance.getTime(picAdapter.infoList[position].dateModified.toLong()*1000,
                            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()))
                }

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    when(newState){
                        SCROLL_STATE_IDLE ->{//静止没有滚动
                            photo_time_tv.visibility = View.GONE
                        }
                        else ->{//滚动
                            photo_time_tv.visibility = View.VISIBLE
                        }
                    }
                }
            })



        }




        loadPicDirectoryAll()
        loadPicturesByBucketId(null)

        //返回按钮
        title_bar_back_iv.rxOnClick {
            onBackPressed()
        }

        title_bar_left_tv.setText(R.string.picture_album_name_all)

        //右边完成按钮
        title_bar_right_tv.apply {

            setText(R.string.picture_complete)

            rxOnClick {
                if(directory_popupwindow_ll.visibility == View.VISIBLE)
                    hideDirectoryPopupwindow()
                else if (picAdapter.itemChooseList.size>0){
                    //
                    for(p in picAdapter.itemChooseList){
                        Log.i("logzh","=============${p}")
                    }
                }
            }
        }

        //选择文件夹
        choose_path_rl.rxOnClick {

            if(directory_popupwindow_ll.visibility == View.VISIBLE)
                hideDirectoryPopupwindow()
            else
                showDirectoryPopupwindow()
        }

        //隐藏选择文件夹
        directory_popupwindow_top_view.rxOnClick {
            if(directory_popupwindow_ll.visibility == View.VISIBLE)
                hideDirectoryPopupwindow()
        }

        //预览
        look_over_tv.rxOnClick {
            if(directory_popupwindow_ll.visibility == View.VISIBLE)
                hideDirectoryPopupwindow()
            else if(picAdapter.itemChooseList.size>0){
                //跳转到预览页面
                skipPhotoLookOverActivity(0,maxChooseNumber,picAdapter.itemChooseList,pageIndex,picAdapter.itemChooseList
                        ,REQUESTCODE_PHOTOLOOKOVER)

            }
        }


    }



    /**
     * 获取所有图片目录
     */
    @SuppressLint("CheckResult")
    fun loadPicDirectoryAll(){
        AlbumLoader.instance.loadPicDirectory().compose(applySchedulers()).subscribe {

            directory_popupwindow_recyclerview.adapter = DirectoryPopupWindowAdapter(this).apply {
                infoList.apply {
                    clear()
                    addAll(it)
                }

                itemClickConsumer = Consumer {
                    hideDirectoryPopupwindow()
                    choose_path_tv.text = it.bucketDisplayName
                    title_bar_left_tv.text = it.bucketDisplayName
                    pageIndex = 0
                    picAdapter.isLoadMore = true
                    //根据目录加载图片
                    loadPicturesByBucketId(it.bucketId)
                }

            }
            hideDirectoryPopupwindow()
        }
    }



    /**
     * 根据目录获取所有图片
     */
    @SuppressLint("CheckResult")
    fun loadPicturesByBucketId(bucketId: String?){
        AlbumLoader.instance.loadPicturesByBucketId(bucketId,pageIndex).compose(applySchedulers()).subscribe {
            if(pageIndex==0) {
                picAdapter.infoList.clear()
                photo_recyclerview.scrollToPosition(0)

                picAdapter.infoList.addAll(it)
                picAdapter.notifyDataSetChanged()
            }else{
                picAdapter.infoList.addAll(it)
                picAdapter.notifyItemRangeInserted(picAdapter.infoList.size-it.size, picAdapter.infoList.size)
            }



            if(it.size<AlbumLoader.PAGE_SIZE){
                picAdapter.isLoadMore = false
            }

        }
    }

    override fun onBackPressed() {
        if(directory_popupwindow_ll.visibility == View.VISIBLE)
            hideDirectoryPopupwindow()
        else
            super.onBackPressed()
    }

    /**
     * 显示目录选择
     */
    fun showDirectoryPopupwindow(){


        directory_popupwindow_ll.visibility = View.VISIBLE

        val mShowAction = TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                1.0f, Animation.RELATIVE_TO_SELF, 0.0f)
                .apply {

                    duration = 300

                    setAnimationListener(object : Animation.AnimationListener{
                        override fun onAnimationRepeat(p0: Animation?) {
                        }

                        override fun onAnimationEnd(p0: Animation?) {
                            directory_popupwindow_ll.setBackgroundColor(Color.parseColor("#c0000000"))
                        }

                        override fun onAnimationStart(p0: Animation?) {
                            directory_popupwindow_ll.setBackgroundColor(Color.parseColor("#00000000"))
                        }
                    })
                }

        directory_popupwindow_ll.animation = mShowAction


    }


    /**
     * 隐藏目录选择
     */
    fun hideDirectoryPopupwindow(){

        directory_popupwindow_ll.visibility = View.GONE

        val mHiddenAction = TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 1.0f)
                .apply {

                    duration = 300

                    setAnimationListener(object : Animation.AnimationListener{
                        override fun onAnimationRepeat(p0: Animation?) {
                        }

                        override fun onAnimationEnd(p0: Animation?) {
                            if(directory_popupwindow_ll.visibility == View.VISIBLE)
                                directory_popupwindow_ll.visibility = View.GONE
                        }

                        override fun onAnimationStart(p0: Animation?) {
                            directory_popupwindow_ll.setBackgroundColor(Color.parseColor("#00000000"))
                        }
                    })
                }
        directory_popupwindow_ll.animation = mHiddenAction



    }


    @SuppressLint("CheckResult")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        //从大图预览返回的数据
        if(requestCode==REQUESTCODE_PHOTOLOOKOVER&&resultCode== Activity.RESULT_OK&&null!=data){
            val chooseList = data.getSerializableExtra("chooseList") as ArrayList<AlbumImage>

            //把原来选中的集合，和现在选中的集合， 添加到一个集合中，用于同步界面
            val oldChooseList = ArrayList<AlbumImage>().apply {
                addAll(picAdapter.itemChooseList.slice(picAdapter.itemChooseList.indices))
                addAll(chooseList.slice(chooseList.indices))
            }


            picAdapter.itemChooseList.apply {
                clear()
                addAll(chooseList)
            }

            //对每一项ui 操作
            Observable.fromIterable(oldChooseList).map {
                picAdapter.infoList.indexOf(it)
            }.compose(applySchedulers()).subscribe {
                if(-1!=it){
                    picAdapter.notifyItemChanged(it)
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

}

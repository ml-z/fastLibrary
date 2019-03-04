package com.ml.lib_picture.ui.photo

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.ml.lib_picture.R
import com.ml.lib_picture.adapter.PhotoLookOverPagerAdapter
import com.ml.lib_picture.entity.AlbumImage
import com.ml.lib_tool.StatusBarUtil
import com.ml.lib_tool.rxOnClick
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.picture_photo_look_over_activity.*
import com.ml.lib_picture.adapter.PhotoLookOverChooseAdapter
import com.ml.lib_picture.body.AlbumLoader
import com.ml.lib_tool.applySchedulers
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity


class PhotoLookOverActivity : RxAppCompatActivity() {


    //当前位置
    private var mCurrentPosition:Int = 0
    //最大的位置
    private var mMaxPosition:Int = 0
    //最大选择的值
    private var mMaxChooseNumber:Int = 0
    /**
     * 开始条数
     */
    var pageIndex:Int = 0
    //是否可以加载
    var isLoadMore = true


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.picture_photo_look_over_activity)



        title_bar_right_tv.text = getString(R.string.picture_complete)

        photo_look_over_bottom_ll.background.alpha = 245
        title_bar_back_rl.background.alpha = 245
        status_bar_view.background.alpha = 245

        StatusBarUtil.instance.setStatusBarView(this,status_bar_view)

        //返回按钮
        title_bar_back_iv.rxOnClick {
            onBackPressed()
        }

        //右边完成按钮
        title_bar_right_tv.rxOnClick {

        }


        //选择按钮
        photo_look_over_choose_cb.setOnCheckedChangeListener { p0, p1 ->

            (photo_look_over_selected_recyclerview.adapter as PhotoLookOverChooseAdapter).apply {

                val info = (photo_look_over_viewpager.adapter as PhotoLookOverPagerAdapter).infoList[this@PhotoLookOverActivity.mCurrentPosition]
                if(p1){//选择
                    val index = infoList.indexOf(info)
                    if(index==-1){
                        infoList.add(info)
                        notifyDataSetChanged()
                        setSelectedRecyclerview(info)
                    }
                }else{
                    //取消
                    val index = infoList.indexOf(info)
                    if(index!=-1){
                        if(index==this.mCurrentPosition){
                            this.mCurrentPosition = -1
                        }
                        infoList.removeAt(index)
                        notifyDataSetChanged()
                    }
                }

                title_bar_right_tv.apply {
                    text = if(infoList.size>0)
                        "${resources.getString(R.string.picture_complete)}(${infoList.size}/$mMaxChooseNumber)"
                    else resources.getString(R.string.picture_complete)

                    setTextColor(if(infoList.size>0) Color.parseColor("#009100") else Color.parseColor("#006000"))
                }

            }

        }


        //设置底部选择图片
        photo_look_over_selected_recyclerview.apply {
            layoutManager = LinearLayoutManager(this@PhotoLookOverActivity).apply { orientation = LinearLayoutManager.HORIZONTAL }

            addItemDecoration(object : RecyclerView.ItemDecoration(){
                override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {

                    val position = parent.getChildAdapterPosition(view)

                    if(position>0){
                        outRect.left = resources.getDimensionPixelSize(R.dimen.dp_15)
                    }

                }
            })

            adapter = PhotoLookOverChooseAdapter(this@PhotoLookOverActivity).apply {
                itemClickConsumer = Consumer {

                    val selectedIndex = (photo_look_over_viewpager.adapter as PhotoLookOverPagerAdapter).infoList.indexOf(infoList[it])

                    if(selectedIndex!=-1){
                        photo_look_over_viewpager.setCurrentItem(selectedIndex,false)

                        setSelectedRecyclerview(infoList[it])

                    }

                }
            }
        }


        photo_look_over_viewpager.apply {
            //适配器
            adapter = PhotoLookOverPagerAdapter(this@PhotoLookOverActivity).apply {
                //图片点击事件
                itemClickConsumer = Consumer {
                    fullScreenLook()
                }
            }
            //滑动监听
            addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
                override fun onPageScrollStateChanged(p0: Int) {
                }

                override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
                }

                @SuppressLint("CheckResult")
                override fun onPageSelected(p0: Int) {

                    this@PhotoLookOverActivity.mCurrentPosition = p0

                    title_bar_left_tv.text = "${p0+1}/$mMaxPosition"

                    val info = (adapter as PhotoLookOverPagerAdapter) .infoList[p0]
                    //预加载图片
                    loadPicturesByBucketId(p0,info)

                    setSelectedRecyclerview(info)

                }

            })
        }

        //防止事件穿透
        photo_look_over_bottom_ll.setOnClickListener {  }


        intent.apply {
            mCurrentPosition = getIntExtra("currentPosition",0)
            val picList = getSerializableExtra("picList") as ArrayList<AlbumImage>
            mMaxChooseNumber = getIntExtra("maxChooseNumber",0)
            //已选择的图片地址
            val chooseList = getSerializableExtra("chooseList") as ArrayList<AlbumImage>
            pageIndex = getIntExtra("pageIndex",0)
            mMaxPosition = picList.size
            if(picList.size==chooseList.size){//点击预览进来，不需要再去查询数据库
                isLoadMore = false
            }

            title_bar_left_tv.text = "${mCurrentPosition+1}/$mMaxPosition"

            initFillPicSelectedList(chooseList)
            initFillPreviewPic(picList)

            if(mCurrentPosition<picList.size){
                setSelectedRecyclerview(picList[mCurrentPosition])
            }

        }
    }


    /**
     * 全屏查看
     */
    fun fullScreenLook(){
        //隐藏
        if(status_bar_view.visibility== View.VISIBLE){

            StatusBarUtil.instance.hiddenStatusBar(this@PhotoLookOverActivity)

            status_bar_view.visibility = View.GONE
            title_bar_back_rl.visibility = View.GONE
            photo_look_over_bottom_ll.visibility = View.GONE

        }
        else{//显示

            StatusBarUtil.instance.showStatusBar(this@PhotoLookOverActivity)

            status_bar_view.visibility = View.VISIBLE
            title_bar_back_rl.visibility = View.VISIBLE
            photo_look_over_bottom_ll.visibility = View.VISIBLE


        }
    }

    /**
     * 设置 底部Recyclerview， 跟随选中状态
     */
    fun setSelectedRecyclerview( info:AlbumImage){

        //在已选中集合里，查找当前图片
        val selectedIndex = (photo_look_over_selected_recyclerview.adapter as PhotoLookOverChooseAdapter).infoList.indexOf(info)

        photo_look_over_choose_cb.isChecked = selectedIndex!=-1

        //同步底部小图选中标识
        if(selectedIndex!=-1){
            //添加选中标识
            photo_look_over_selected_recyclerview.apply {
                smoothScrollToPosition(selectedIndex)
                (adapter as PhotoLookOverChooseAdapter).apply {
                    val oldPosition = this.mCurrentPosition
                    this.mCurrentPosition = selectedIndex
                    notifyItemChanged(oldPosition)
                    notifyItemChanged(selectedIndex)
                }
            }
        }else{
            //取消选中标识
            (photo_look_over_selected_recyclerview.adapter as PhotoLookOverChooseAdapter).apply {
                val oldPosition = this.mCurrentPosition
                this.mCurrentPosition = -1
                notifyItemChanged(oldPosition)
            }
        }

    }


    /**
     * 加载图片
     */
    @SuppressLint("CheckResult")
    @Synchronized fun  loadPicturesByBucketId(p0:Int,p1:AlbumImage){

        pageIndex++

        //预加载
        if(isLoadMore&&p0>=photo_look_over_viewpager.adapter!!.count-16){
            AlbumLoader.instance.loadPicturesByBucketId(p1.bucketId,pageIndex).compose(applySchedulers()).subscribe {

                (photo_look_over_viewpager.adapter as PhotoLookOverPagerAdapter).apply {
                    infoList.addAll(it)
                    notifyDataSetChanged()
                    this@PhotoLookOverActivity.mMaxPosition = infoList.size
                }


                title_bar_left_tv.text = "${mCurrentPosition+1}/${this@PhotoLookOverActivity.mMaxPosition}"

                if(it.size<AlbumLoader.PAGE_SIZE){
                    isLoadMore = false
                }
            }
        }
    }

    /**
     * 填充viewpager数据
     */
    fun initFillPreviewPic(picList:ArrayList<AlbumImage>){
        (photo_look_over_viewpager.adapter as PhotoLookOverPagerAdapter).apply {
            infoList.addAll(picList)
            notifyDataSetChanged()

            if(infoList.size>mCurrentPosition){
                photo_look_over_viewpager.setCurrentItem(mCurrentPosition,true)

                val info = infoList[mCurrentPosition]
                photo_look_over_choose_cb.isChecked = (photo_look_over_selected_recyclerview.adapter as PhotoLookOverChooseAdapter).infoList.indexOf(info)!=-1

                //预加载图片
                loadPicturesByBucketId(mCurrentPosition,info)
            }
        }
    }

    /**
     * 填充底部选中的图片数据
     */
    fun initFillPicSelectedList(chooseList:ArrayList<AlbumImage>){

        //底部图片
        (photo_look_over_selected_recyclerview.adapter as PhotoLookOverChooseAdapter).apply {

            infoList.addAll(chooseList)
            notifyDataSetChanged()
        }

        if(chooseList.size==0){
            photo_look_over_selected_recyclerview.visibility = View.GONE
        }else{
            photo_look_over_selected_recyclerview.visibility = View.VISIBLE
        }

    }


    override fun onBackPressed() {
        val intent = Intent().apply {
            putExtra("chooseList",(photo_look_over_selected_recyclerview.adapter as PhotoLookOverChooseAdapter).infoList)
        }
        setResult(Activity.RESULT_OK,intent)

        finish()
    }


}

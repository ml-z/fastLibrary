package com.ml.lib_picture.body

import android.annotation.SuppressLint
import android.provider.MediaStore
import android.text.TextUtils
import com.ml.lib_picture.R
import com.ml.lib_picture.entity.AlbumFile
import com.ml.lib_picture.entity.AlbumImage
import com.ml.lib_tool.FileSizeUtil
import com.ml.lib_tool.getApplicationContext
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.functions.Function


class AlbumLoader private constructor(){

    companion object {
        val instance   by lazy { AlbumLoader() }
        //加载图片分页的大小
        const val PAGE_SIZE = 100
    }


    /**
     * 查询图片的目录
     */
    @SuppressLint("CheckResult")
    fun loadPicDirectory():Observable<ArrayList<AlbumFile>>{

        return loadPicDirectoryAll()
                .flatMap(object : Function<AlbumFile, ObservableSource<ArrayList<AlbumFile>>>{
                    override fun apply(albumFileAll: AlbumFile): ObservableSource<ArrayList<AlbumFile>> {

                        return Observable.create {

                            val mCursor = getApplicationContext().contentResolver.query(
                                    MediaStore.Files.getContentUri("external"),
                                    arrayOf("COUNT(${ MediaStore.Files.FileColumns._ID }) AS fileCount",
                                            MediaStore.Images.ImageColumns.BUCKET_ID,
                                            MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                                            MediaStore.Images.ImageColumns.MINI_THUMB_MAGIC,
                                            "${MediaStore.Files.FileColumns.DATA} FROM (SELECT *")
                                    ,"${MediaStore.Files.FileColumns.MEDIA_TYPE} = ${MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE}) ORDER BY ${MediaStore.Files.FileColumns.DATE_MODIFIED }  ) GROUP BY ( ${MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME}",
                                    null,
                                    "${MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME} DESC"
                            )

                            //图片目录集合
                            val albumFileList = ArrayList<AlbumFile>()
                            albumFileList.add(albumFileAll)
                            //图片目录名称map，防止同样的名称
                            val albumFileNameMap = HashMap<String,String>()
                            albumFileNameMap[albumFileAll.bucketDisplayName] = fileNameIsExist(albumFileNameMap, albumFileAll.bucketDisplayName)


                            mCursor?.apply {

                                while (moveToNext()) {

                                    //上级目录名称
                                    val fileName = fileNameIsExist(albumFileNameMap, mCursor.getString(2))
                                    //保存此次目录名称
                                    albumFileNameMap[fileName] = fileName
                                    //图片路径
                                    val data = mCursor.getString(4)
                                    // 图片数量，目录名称，小图路径，图片路径
                                    val albumFile =  AlbumFile(mCursor.getInt(0),mCursor.getString(1),fileName,mCursor.getString(3),data)
                                    //加入集合
                                    albumFileList.add(albumFile)

                                }
                            }

                            //清空map
                            albumFileNameMap.clear()

                            it.onNext(albumFileList)
                        }

                    }
                })
    }


    /**
     * 查询全部分类目录
     */
    @SuppressLint("CheckResult")
    private fun loadPicDirectoryAll():Observable<AlbumFile>{

        return Observable.create {

            val sortOrder = "${MediaStore.Images.Media.DATE_MODIFIED} DESC "

            val mCursor = getApplicationContext().contentResolver.query(
                    MediaStore.Files.getContentUri("external"),
                    arrayOf("COUNT(${ MediaStore.Files.FileColumns.PARENT }) AS fileCount",
                            MediaStore.Images.ImageColumns.BUCKET_ID,
                            MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                            MediaStore.Images.ImageColumns.MINI_THUMB_MAGIC,
                           MediaStore.Files.FileColumns.DATA )
                    ,"${MediaStore.Files.FileColumns.MEDIA_TYPE} = ${MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE} ",
                    null,
                    sortOrder
            )

            //图片目录集合
            val albumFileList = ArrayList<AlbumFile>()

            mCursor?.apply {

                while (moveToNext()) {

                    //图片路径
                    val data = mCursor.getString(4)
                    // 图片数量，目录名称，小图路径，图片路径
                    val albumFile =  AlbumFile(mCursor.getInt(0),mCursor.getString(1),mCursor.getString(2),mCursor.getString(3),data)
                    //加入集合
                    albumFileList.add(albumFile)

                }
            }
            if(albumFileList.isNotEmpty()){
                val albumFile = albumFileList[0]
                albumFile.bucketDisplayName = getApplicationContext().resources.getString(R.string.picture_album_name_all)
                albumFile.bucketId = null
                it.onNext(albumFile)
            }

        }
    }


    /**
     * 查询某个文件夹下的所有图片
     * bucketId 为null时，查询全部相片
     */
    @SuppressLint("CheckResult")
    fun loadPicturesByBucketId(bucketId: String?,pageIndex:Int):Observable<ArrayList<AlbumImage>>{

        return Observable.create<ArrayList<AlbumImage>> {

            val sortOrder = "${MediaStore.Images.Media.DATE_MODIFIED} DESC limit $PAGE_SIZE offset ${pageIndex * PAGE_SIZE} "

            var selection = if(null==bucketId) null else "${MediaStore.Images.ImageColumns.BUCKET_ID} = '$bucketId'"

            val mCursor = getApplicationContext().contentResolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    arrayOf(MediaStore.Images.ImageColumns.DATA,MediaStore.Images.ImageColumns.MINI_THUMB_MAGIC
                    ,MediaStore.Images.ImageColumns.SIZE,MediaStore.Images.ImageColumns.DATE_MODIFIED,MediaStore.Images.ImageColumns.BUCKET_ID),
                    selection
                    ,
                    null,
                    sortOrder
            )

            val albumImageList = ArrayList<AlbumImage>()

            mCursor?.apply {
                while (moveToNext()) {
                   val albumImage =  AlbumImage(mCursor.getString(0),mCursor.getString(1),
                       FileSizeUtil.instance.formetFileSize(mCursor.getString(2).toLong()),
                           mCursor.getString(3),mCursor.getString(4))

                    albumImageList.add(albumImage)
                }
            }

            it.onNext(albumImageList)

        }

    }





    /**
     * 查询名称是否存在
     */
    private fun fileNameIsExist(albumFileNameMap : HashMap<String,String>,fileName:String):String{
        if(TextUtils.isEmpty(albumFileNameMap[fileName])){
            return fileName
        }
        return fileNameIsExist(albumFileNameMap,"${fileName}X")
    }

}
package com.ml.lib_net

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.logging.HttpLoggingInterceptor



class RxHttp private constructor(){

    companion object {
        val instance: RxHttp by lazy { RxHttp() }
    }

    private  var mRetrofit:Retrofit? = null
    private  var isDebug:Boolean = false
    private  var baseUrl:String = "http://www.baidu.com/"

    fun initRxHttp(isDebug : Boolean,httpBuilder:OkHttpClient.Builder?=null){

        this@RxHttp.isDebug = isDebug
        createRetrofit(httpBuilder = httpBuilder)
    }


    private fun createRetrofit( httpBuilder:OkHttpClient.Builder?=null){

        val builder = httpBuilder ?: OkHttpClient.Builder()

        if(isDebug){
            //显示日志
            val logInterceptor =  HttpLoggingInterceptor()
            logInterceptor.level = HttpLoggingInterceptor.Level.BODY

            builder.addInterceptor(logInterceptor)
        }

        //处理多BaseUrl,添加应用拦截器
        builder.addInterceptor( BaseUrlInterceptor())

        mRetrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

    }


    fun <T> createService(service: Class<T>): T {

        if(null==mRetrofit){
            createRetrofit()
        }

        return mRetrofit!!.create(service)

    }

}



package com.ml.lib_net

import com.ml.lib_net.RxHttpConfig.Companion.URL_NAME
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.HttpUrl


/**
 * BaseUrl替换拦截器
 */
internal class BaseUrlInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        //获取原始的originalRequest
        val originalRequest = chain.request()
        //获取老的url
        val oldUrl = originalRequest.url()
        //获取originalRequest的创建者builder
        val builder = originalRequest.newBuilder()
        //获取头信息的集合如:manage,mdffx
        val urlnameList = originalRequest.headers(URL_NAME)

        if (urlnameList != null && urlnameList.size > 0) {
            //删除原有配置中的值,就是namesAndValues集合里的值
            builder.removeHeader(URL_NAME)
            //获取头信息中配置的value,如:manage或者mdffx
            val urlNew = urlnameList[0]
            val baseURL: HttpUrl = HttpUrl.parse(urlNew)!!
            //重建新的HttpUrl,需要重新设置的url部分
            val newHttpUrl = oldUrl.newBuilder()
                    .scheme(baseURL.scheme())//http协议如:http或者https
                    .host(baseURL.host())//主机地址
                    .port(baseURL.port())//端口
                    .build()
            //获取处理后的新newRequest
            val newRequest = builder.url(newHttpUrl).build()
            return chain.proceed(newRequest)
        } else {
            return chain.proceed(originalRequest)
        }
    }
}
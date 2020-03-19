package com.xing.library.helper.network

import com.xing.library.helper.network.RequestHandler
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody
import java.io.IOException


/**
 * 页面描述：默认拦截器
 *
 *
 * Created by ditclear on 2017/7/28.
 */

class NetInterceptor(private val handler: RequestHandler?) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()


        if (handler != null) {
            request = handler.onBeforeRequest(request, chain)
        }
        val response = chain.proceed(request)

         if (handler != null) {
            val tmp = handler.onAfterRequest(response, chain)
            return tmp
        }
        response.body()?.apply {
            return response.newBuilder().body(ResponseBody.create(contentType(), this.string()))
                .build()
        }
        return response
    }
}

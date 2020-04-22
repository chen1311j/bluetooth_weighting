package com.xing.library.model.remote.api

import com.xing.library.model.data.BaseResponse
import com.xing.library.model.data.CustomerBean
import com.xing.library.model.data.DefaultCode
import io.reactivex.Single
import retrofit2.http.*

interface ApiMailService {
    @GET("/change_area.php")
    fun setPreviousOrder(@Query("company") company:String): Single<BaseResponse<Any>>
}
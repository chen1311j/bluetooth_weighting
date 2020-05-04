package com.xing.library.model.remote.api

import com.xing.library.model.data.BaseResponse
import com.xing.library.model.data.CustomerBean
import com.xing.library.model.data.DefaultCode
import io.reactivex.Single
import retrofit2.http.*

interface ApiService {

    @GET("/company/index1")
    fun getCustomerList(): Single<BaseResponse<List<CustomerBean>>>


    @POST("/company/weight")
    @FormUrlEncoded
    fun requestWeight(@FieldMap map: Map<String, String>): Single<BaseResponse<Any>>

}
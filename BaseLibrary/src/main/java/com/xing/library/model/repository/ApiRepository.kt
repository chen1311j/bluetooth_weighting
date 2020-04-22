package com.xing.library.model.repository

import com.xing.library.model.data.BaseResponse
import com.xing.library.model.remote.api.ApiMailService
import com.xing.library.model.remote.api.ApiService
import io.reactivex.Single

class  ApiRepository constructor(private val remote: ApiService,private val remote2: ApiMailService){

    fun getCustomerList() = remote.getCustomerList()


    fun requestWeight(map: Map<String, String>) = remote.requestWeight(map)

    fun setPreviousOrder(company:String) = remote2.setPreviousOrder(company)
}

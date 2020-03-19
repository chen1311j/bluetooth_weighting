package com.xing.library.model.repository

import com.xing.library.model.remote.api.ApiService

class ApiRepository constructor(private val remote: ApiService) {

    fun getCustomerList() = remote.getCustomerList()


    fun requestWeight(map: Map<String, String>) = remote.requestWeight(map)
}

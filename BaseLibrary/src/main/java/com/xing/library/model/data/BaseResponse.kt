package com.xing.library.model.data

/**
 * 页面描述：BaseResponse
 *
 * Created by ditclear on 2017/10/11.
 */
open class BaseResponse<out T : Any> {
    val statua: String? = null
    val data: T? = null
    val error_code = 0
    val error = ""
}

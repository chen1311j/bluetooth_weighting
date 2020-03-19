package com.xing.library.helper.network

import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

class NullOnEmptyConvertFactory : Converter.Factory() {
    override fun responseBodyConverter(
        type: Type?,
        annotations: Array<Annotation>?,
        retrofit: Retrofit?
    ): Converter<ResponseBody, *>? {

        val delegate = retrofit!!.nextResponseBodyConverter<ResponseBody>(this, type!!, annotations!!)as Converter<ResponseBody, *>
        return object :Converter<ResponseBody,Any>{
            override fun convert(value: ResponseBody): Any? {
                return  if (value.contentLength() == 0L) "" else  delegate.convert(value)
            }
        }
    }

}

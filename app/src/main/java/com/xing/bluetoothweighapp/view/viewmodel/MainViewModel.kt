/**
 * generate by AAMVVM: https://github.com/HeadingMobile/AAMVVM
 */
package com.xing.bluetoothweighapp.view.viewmodel

import androidx.databinding.ObservableField
import com.google.gson.Gson
import com.xing.bluetoothweighapp.dao.WeightOrderBeanDao
import com.xing.bluetoothweighapp.db.WeightBeanManager
import com.xing.bluetoothweighapp.db.entity.WeightOrderBean
import com.xing.library.helper.extens.async
import com.xing.library.helper.utils.GsonUtil
import com.xing.library.model.data.DefaultCode
import com.xing.library.model.data.UploadBean
import com.xing.library.model.repository.ApiRepository
import com.xing.library.viewmodel.BaseViewModel
import io.reactivex.Single

/**
 * description: MainViewModel
 *
 * @see com.xing.bluetoothweighapp.MainActivity
 *
 * @date 2020/02/21
 */

class MainViewModel constructor(private val dao: WeightBeanManager, val rope: ApiRepository) : BaseViewModel() {

    companion object {
        const val NONE_DATA = 110
    }

    val map = HashMap<String, String>()
    val noneUploadCount = ObservableField<Int>()



    fun getNoneCount()=dao.query("WHERE ${WeightOrderBeanDao.Properties.Status.columnName}=?", WeightBeanManager.NONE.toString())

    fun uploadWeight(): Single<Any> = dao.query(
        "WHERE ${WeightOrderBeanDao.Properties.Status.columnName}=?",
        WeightBeanManager.NONE.toString()
    ).flatMap { list ->
        if (list.isEmpty()) {
            return@flatMap Single.just("")
        }
        map["data"] = GsonUtil.GsonString(list.map { bean -> UploadBean(bean.orderId, bean.weigth, bean.customer, bean.time) })
        return@flatMap rope.requestWeight(map)
            .async().doOnSuccess { response ->
                val newList = list.map {
                    it.status = WeightBeanManager.UPLOAD
                    it
                }.toList()
                dao.saveOrUpdate(newList)
            }
    }
}
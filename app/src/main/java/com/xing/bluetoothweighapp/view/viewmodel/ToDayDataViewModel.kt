package com.xing.bluetoothweighapp.view.viewmodel

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import com.xing.bluetoothweighapp.dao.WeightOrderBeanDao
import com.xing.bluetoothweighapp.db.WeightBeanManager
import com.xing.library.helper.extens.async
import com.xing.library.helper.utils.TimeUtil
import com.xing.library.viewmodel.BaseViewModel
import io.reactivex.Single

class ToDayDataViewModel(val dao: WeightBeanManager) : BaseViewModel() {

    val todayDataList = ObservableArrayList<ItemTodayWeightViewModel>()
    val isEmpty = ObservableField<Boolean>(false)


    fun getTodayDataList(): Single<List<ItemTodayWeightViewModel>> = dao.queryAll().map { orderList ->
        orderList.filter {
            val time = TimeUtil.getFormatTimeYY(TimeUtil.getFormatLongTimeAll(it.time))
            val currentTime = TimeUtil.getFormatTimeYY(System.currentTimeMillis())
            time==currentTime
        }.map { ItemTodayWeightViewModel(it) }
    }.doOnSuccess {
        isEmpty.set(it.isNullOrEmpty())
    }

    fun deleteTodayItem(item: ItemTodayWeightViewModel){
        dao.deleteByKey(item.id)
    }

}
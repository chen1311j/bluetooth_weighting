package com.xing.bluetoothweighapp.view.viewmodel

import com.xing.bluetoothweighapp.db.entity.WeightOrderBean
import com.xing.library.viewmodel.BaseViewModel

class ItemTodayWeightViewModel( bean: WeightOrderBean) :BaseViewModel() {

    val   orderCode  =bean.orderId
    val   weight  =bean.weigth
    val   name  =bean.customer
}
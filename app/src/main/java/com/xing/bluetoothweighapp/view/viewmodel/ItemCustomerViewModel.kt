package com.xing.bluetoothweighapp.view.viewmodel

import com.xing.library.model.data.CustomerBean
import com.xing.library.viewmodel.BaseViewModel

class ItemCustomerViewModel( bean: CustomerBean) : BaseViewModel() {

    val name = bean.company_name
}

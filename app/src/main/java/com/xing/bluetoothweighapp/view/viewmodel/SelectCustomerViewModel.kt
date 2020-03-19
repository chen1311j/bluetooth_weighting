/**
 * generate by AAMVVM: https://github.com/HeadingMobile/AAMVVM
 */
package com.xing.bluetoothweighapp.view.viewmodel

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import com.xing.library.helper.extens.async
import com.xing.library.model.repository.ApiRepository
import com.xing.library.viewmodel.BaseViewModel
import io.reactivex.Single

/**
 * description: SelectSustomerViewModel
 *
 * @see com.xing.bluetoothweighapp.view.SelectCustomerActivity
 *
 * @date 2020/02/21
 */

class SelectCustomerViewModel constructor(val repo: ApiRepository) : BaseViewModel() {

    val customerList = ObservableArrayList<ItemCustomerViewModel>()


    fun getCustomerList(): Single<List<ItemCustomerViewModel>?> = repo.getCustomerList()
        .async().map { it.data?.map { ItemCustomerViewModel(it) }?.toList() }

}
/**
 * generate by AAMVVM: https://github.com/HeadingMobile/AAMVVM
 */
package com.xing.bluetoothweighapp.view.viewmodel

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.xing.bluetoothweighapp.dao.WeightOrderBeanDao
import com.xing.bluetoothweighapp.db.WeightBeanManager
import com.xing.bluetoothweighapp.db.entity.WeightOrderBean
import com.xing.library.helper.extens.bindLifeCycle
import com.xing.library.helper.extens.get
import com.xing.library.helper.extens.logD
import com.xing.library.helper.extens.toFlowable
import com.xing.library.helper.utils.TimeUtil
import com.xing.library.viewmodel.BaseViewModel
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import io.reactivex.functions.Function3

/**
 * description: WeighScancodeViewModel
 *
 * @see com.xing.bluetoothweighapp.view.WeighScanCodeActivity
 *
 * @date 2020/02/21
 */

class WeighScanCodeViewModel constructor(private val dao: WeightBeanManager) : BaseViewModel() {

    val orderCode = ObservableField<String>()
    val weight = ObservableField<String>("0.0")
    val customerName = ObservableField<String>()
    val isAutoSave = MutableLiveData<Boolean>(true)
    val isEnabled = ObservableField<Boolean>()
    val isSaveSuccess = ObservableField<Int>()
    val scanCount = ObservableField<Int>(0)
    var isResetScanning = false

    companion object {
        const val SAVE_SUCCESS = 1
        const val SAVE_FAILURE = 2
        const val REPEAT_FAILURE = 4
        const val RESET = 3
    }

    fun getCount() = dao.query("WHERE ${WeightOrderBeanDao.Properties.Status.columnName}=?", WeightBeanManager.NONE.toString())

    fun checkData(): Flowable<Boolean> = Flowable.combineLatest(orderCode.toFlowable(), weight.toFlowable(), customerName.toFlowable(),
        Function3<String, String, String, Boolean> { t1, t2, t3 ->
            return@Function3 (t1.isNotEmpty() && t2.isNotEmpty() && t3.isNotEmpty())
        }).doAfterNext {
        isEnabled.set(it)
        if (it && isAutoSave.get() == true) {
            if (isResetScanning) {
                resetSaveData()
            } else {
                saveData()
            }
        }
    }

    fun checkSave(): Flowable<Int> = isSaveSuccess.toFlowable()

    fun saveData() {
        if (orderCode.get().isNullOrEmpty()) {
            return
        }
        val subscribe = dao.query("WHERE ${WeightOrderBeanDao.Properties.OrderId.columnName}=?", orderCode.get().toString()).subscribe({
            if (!it.isNullOrEmpty()) {
                val oldWeight = it[0].weigth.toDouble()
                val newWeight = weight.get()?.toDouble()
                newWeight?.let {
                    if (oldWeight >= newWeight) {
                        isSaveSuccess.set(REPEAT_FAILURE)
                        return@subscribe
                    }
                }
            }
            save()
        }, {})
    }

    private fun save() {
        if (weight.get()?.equals("0.0") != true) {
            val entityId = dao.saveOrUpdate(
                WeightOrderBean(
                    null,
                    orderCode.get(), TimeUtil.getFormatTimeAll(System.currentTimeMillis()),
                    weight.get(), customerName.get(), WeightBeanManager.NONE
                )
            )
            isSaveSuccess.set(
                if (entityId != -1L) {
                    SAVE_SUCCESS
                } else
                    SAVE_FAILURE
            )
        } else {
            isSaveSuccess.set(REPEAT_FAILURE)
        }
    }

    fun resetSaveData() {
        if (orderCode.get().isNullOrEmpty()) {
            return
        }
        val subscribe = dao.queryAll().map { orderList ->
            orderList.filter {
                val time = TimeUtil.getFormatTimeYY(TimeUtil.getFormatLongTimeAll(it.time))
                val currentTime = TimeUtil.getFormatTimeYY(System.currentTimeMillis())
                time == currentTime
            }.filter { it.orderId == orderCode.get() }
        }.subscribe({
            if (!it.isNullOrEmpty()) {
                val oldWeight = it[0].weigth.toDouble()
                val newWeight = weight.get()?.toDouble()
                newWeight?.let {
                    if (oldWeight >= newWeight) {
                        isSaveSuccess.set(SAVE_FAILURE)
                        return@subscribe
                    }
                }
                save()
            }else{
                isSaveSuccess.set(SAVE_FAILURE)
            }
        }, {

        })

    }

   /* private fun resetData() {
        isSaveSuccess.set(RESET)
        //orderCode.set("")
        //weight.set("0.0")
        //scanCount.set(dao.count().toInt())
    }*/
}
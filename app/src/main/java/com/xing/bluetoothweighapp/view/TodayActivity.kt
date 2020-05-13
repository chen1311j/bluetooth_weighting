package com.xing.bluetoothweighapp.view

import android.os.Bundle
import android.util.Log
import android.view.View
import com.xing.bluetoothweighapp.R
import com.xing.bluetoothweighapp.databinding.ActivityTodayBinding
import com.xing.bluetoothweighapp.view.viewmodel.ItemCustomerViewModel
import com.xing.bluetoothweighapp.view.viewmodel.ItemTodayWeightViewModel
import com.xing.bluetoothweighapp.view.viewmodel.ToDayDataViewModel
import com.xing.library.helper.adapter.recyclerview.ItemChildClickPresenter
import com.xing.library.helper.adapter.recyclerview.SingleTypeAdapter
import com.xing.library.helper.extens.bindLifeCycle
import com.xing.library.view.base.BaseActivity
import kotlinx.android.synthetic.main.activity_today.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class TodayActivity : BaseActivity<ActivityTodayBinding>(),
    ItemChildClickPresenter<ItemTodayWeightViewModel> {
    override fun onChildItemClick(v: View?, item: ItemTodayWeightViewModel) {
        when(v?.id){
            R.id.iv_weight_delete ->{
                Log.i("tag", "删除数据")
            }
        }
    }

    private val mViewModel by viewModel<ToDayDataViewModel>()

    private val adapter: SingleTypeAdapter<ItemTodayWeightViewModel> by lazy {
        SingleTypeAdapter<ItemTodayWeightViewModel>(this, R.layout.item_today_weight_layout, mViewModel.todayDataList).apply {
            itemChildPresenter = this@TodayActivity
        }
    }

    override fun getLayoutId(): Int = R.layout.activity_today
    override fun loadData(isRefresh: Boolean) {

    }

    override fun initView() {
        mBinding.vm = mViewModel
        recycler_today.adapter = adapter
        mViewModel.getTodayDataList().bindLifeCycle(this).subscribe({
            mViewModel.todayDataList.addAll(it)
        }, {

        })
    }

}

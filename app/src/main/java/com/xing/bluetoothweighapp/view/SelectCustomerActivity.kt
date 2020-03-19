/**
 * generate by AAMVVM: https://github.com/HeadingMobile/AAMVVM
 */
package com.xing.bluetoothweighapp.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import com.xing.bluetoothweighapp.ProgressView
import com.xing.bluetoothweighapp.R
import com.xing.bluetoothweighapp.databinding.SelectCustomerActivityBinding
import com.xing.bluetoothweighapp.view.viewmodel.ItemCustomerViewModel
import com.xing.bluetoothweighapp.view.viewmodel.SelectCustomerViewModel
import com.xing.library.helper.Constants
import com.xing.library.helper.adapter.recyclerview.ItemClickPresenter
import com.xing.library.helper.adapter.recyclerview.SingleTypeAdapter
import com.xing.library.helper.extens.bindLifeCycle
import com.xing.library.helper.extens.navigateToActivity
import com.xing.library.view.base.BaseActivity
import kotlinx.android.synthetic.main.select_customer_activity.*
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * description: SelectSustomerActivity
 * @see SelectCustomerViewModel
 *
 * @date 2020/02/21
 */

class SelectCustomerActivity : BaseActivity<SelectCustomerActivityBinding>(), ItemClickPresenter<ItemCustomerViewModel> {


    companion object {
        const val REQUEST_CODE = 1000
    }

    private val progress: ProgressView by lazy {
        ProgressView(this)
    }

    override fun onItemClick(v: View?, item: ItemCustomerViewModel) {
        val bundle = Bundle()
        bundle.putString(Constants.NAME,item.name)
        bundle.putBoolean(Constants.IS_RESET_SCANNING, intent.getSerializableExtra(Constants.KEY_SERIALIZABLE) as Boolean)
        navigateToActivity(WeighScanCodeActivity::class.java, bundle, REQUEST_CODE)
    }

    private val mViewModel by viewModel<SelectCustomerViewModel>()

    override fun getLayoutId(): Int = R.layout.select_customer_activity

    private val adapter: SingleTypeAdapter<ItemCustomerViewModel> by lazy {
        SingleTypeAdapter<ItemCustomerViewModel>(this, R.layout.item_customer_layout, mViewModel.customerList).apply {
            itemPresenter = this@SelectCustomerActivity
        }
    }

    override fun initView() {
        mBinding.vm = mViewModel
        val itemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        //itemDecoration.setDrawable(resources.getDrawable(R.color.color_f5f5f5))
        recycler_customer.addItemDecoration(itemDecoration)
        recycler_customer.adapter = adapter
    }


    override fun loadData(isRefresh: Boolean) {
        mViewModel.getCustomerList()
            .doOnDispose {
                progress.show()
            }
            .doFinally {
                refresh_layout.finishRefresh()
            }
            .bindLifeCycle(this)
            .subscribe({ items ->
                progress.cancel()
                items?.apply {
                    mViewModel.customerList.clear()
                    mViewModel.customerList.addAll(this)
                }
            }, {
                progress.cancel()
                toastFailure(it)
            })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            finish()
        }
    }
}

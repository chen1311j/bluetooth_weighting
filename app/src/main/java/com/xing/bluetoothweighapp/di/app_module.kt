package com.xing.bluetoothweighapp.di

import com.xing.bluetoothweighapp.db.ManagerFactory
import com.xing.bluetoothweighapp.view.viewmodel.MainViewModel
import com.xing.bluetoothweighapp.view.viewmodel.SelectCustomerViewModel
import com.xing.bluetoothweighapp.view.viewmodel.ToDayDataViewModel
import com.xing.bluetoothweighapp.view.viewmodel.WeighScanCodeViewModel
import com.xing.library.helper.Constants
import com.xing.library.helper.network.NetMgr
import com.xing.library.model.remote.api.ApiMailService
import com.xing.library.model.remote.api.ApiService
import com.xing.library.model.repository.ApiRepository
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import retrofit2.Retrofit

val viewModelModule = module {

    viewModel { MainViewModel(get(),get()) }
    viewModel { SelectCustomerViewModel(get()) }
    viewModel { WeighScanCodeViewModel(get(),get()) }
    viewModel { ToDayDataViewModel(get()) }

}

val remoteModule = module {
    single<Retrofit> { NetMgr.getRetrofit(Constants.HOST_API) }
    single<ApiService> { get<Retrofit>().create(ApiService::class.java) }
    single<ApiMailService> { NetMgr[Constants.HOST_API_2, ApiMailService::class.java] }
}


val repoModule = module {
    single { ApiRepository(get(),get()) }
    single { ManagerFactory.getInstance().weigthBeanManager }
}


val appModule = listOf(viewModelModule, remoteModule, repoModule)

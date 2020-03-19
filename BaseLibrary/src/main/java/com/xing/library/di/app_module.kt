package com.xing.library.di

import org.koin.dsl.module.module

val viewModelModule = module {



}

val remoteModule = module {

}


val repoModule = module {

}


val appModule = listOf(viewModelModule, remoteModule, repoModule)

package com.xing.library.view.base

import android.view.View

/**
 * 页面描述：Presenter
 *
 * Created by ditclear on 2017/11/2.
 */
interface Presenter:View.OnClickListener{

    fun loadData(isRefresh:Boolean)

    override fun onClick(v: View?)

    //fun variableId(): Int
}
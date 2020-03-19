package com.xing.library.helper.adapter.viewpager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter

/**
 * 页面描述：fragment PagerAdapter
 *
 * Created by ditclear on 2017/9/30.
 */

abstract class AbstractPagerAdapter(fm: androidx.fragment.app.FragmentManager, var title: Array<String>) :
    FragmentStatePagerAdapter(fm) {
    var list :MutableList<Fragment?> = mutableListOf()

    init {
        title.iterator().forEach { list.add(null) }
    }

    override fun getCount(): Int = title.size

    abstract override fun getItem(pos: Int): Fragment

    override fun getPageTitle(position: Int): CharSequence = title[position]

}
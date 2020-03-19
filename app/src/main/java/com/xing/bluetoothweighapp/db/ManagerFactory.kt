package com.xing.bluetoothweighapp.db

import com.xing.bluetoothweighapp.Application


class ManagerFactory private constructor() {

    /**
     * 每一个BeanManager都管理着数据库中的一个表，我将这些管理者在ManagerFactory中进行统一管理
     */
    val weigthBeanManager: WeightBeanManager
        @Synchronized
        get() = WeightBeanManager(DBMangaer.getInstance(Application.application).getDaoSession().weightOrderBeanDao)


    companion object {
        private var managerFactory: ManagerFactory? = null

        fun getInstance(): ManagerFactory {
            if (managerFactory == null) {
                synchronized(ManagerFactory::class) {
                    if (managerFactory == null) {
                        managerFactory = ManagerFactory()
                    }
                }
            }

            return managerFactory!!
        }
    }
}

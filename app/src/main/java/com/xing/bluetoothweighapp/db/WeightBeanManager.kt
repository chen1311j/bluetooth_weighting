package com.xing.bluetoothweighapp.db

import com.xing.bluetoothweighapp.dao.WeightOrderBeanDao
import com.xing.bluetoothweighapp.db.entity.WeightOrderBean
import com.xing.library.db.BaseBeanManger
import org.greenrobot.greendao.AbstractDao

class WeightBeanManager(mDao: AbstractDao<WeightOrderBean, Long>) : BaseBeanManger<WeightOrderBean, Long>(mDao) {

    companion object {
        const val   UPLOAD = 1
        const val   NONE = 2
    }
}
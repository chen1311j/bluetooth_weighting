package com.xing.bluetoothweighapp.db

import android.annotation.SuppressLint
import android.content.Context
import com.xing.bluetoothweighapp.dao.DaoMaster
import com.xing.bluetoothweighapp.dao.DaoSession
import org.greenrobot.greendao.query.QueryBuilder


class DBMangaer private constructor(val context: Context) {


    private var mDaoSession: DaoSession? = null
    private var mDaoMaster: DaoMaster? = null
    private var mHelper: DaoMaster.DevOpenHelper? = null

    companion object {
        private const val DB_NAME = "bluetooth_weigh.db"//数据库名称
        @SuppressLint("StaticFieldLeak")
        private var dbManager: DBMangaer? = null

        /**
         * 使用单例模式获得操作数据库的对象
         *
         * @return
         */
        fun getInstance(context: Context): DBMangaer {
            if (dbManager == null) {
                synchronized(DBMangaer::class) {
                    if (dbManager == null) {
                        dbManager =
                            DBMangaer(context)
                    }
                }
            }
            return dbManager!!
        }

        fun getDBName():String{
            return DB_NAME
        }
    }

    /**
     * 获取DaoSession
     *
     * @return
     */
    @Synchronized
    fun getDaoSession(): DaoSession {
        if (mDaoSession == null) {
            mDaoSession = getDaoMaster().newSession()
        }
        return mDaoSession!!
    }

    /**
     * 设置debug模式开启或关闭，默认关闭
     *
     * @param flag
     */
    fun setBug(flag: Boolean) {
        QueryBuilder.LOG_SQL = flag
        QueryBuilder.LOG_VALUES = flag
    }

    /**
     * 关闭数据库
     */
    @Synchronized
    fun closeDataBease() {
        closeDaoSession()
        closeHelper()
    }

    private fun closeDaoSession() {
        mDaoSession?.clear()
        mDaoSession = null
    }


    private fun closeHelper() {
        mHelper?.close()
        mHelper = null
    }

    /**
     * 判断数据库是否存在，如果不存在则创建
     *
     * @return
     */
    fun getDaoMaster(): DaoMaster {
        if (mHelper == null) {
            mHelper = DaoMaster.DevOpenHelper(context, DB_NAME)
            mDaoMaster = DaoMaster(mHelper?.writableDb)
        }
        return mDaoMaster!!
    }

}
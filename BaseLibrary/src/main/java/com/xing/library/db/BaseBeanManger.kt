package com.xing.library.db

import io.reactivex.Flowable
import io.reactivex.Single
import org.greenrobot.greendao.AbstractDao

abstract class BaseBeanManger<T, K> constructor(private var mDao: AbstractDao<T, K>) {

    fun save(item: T) {
        mDao.insert(item)
    }

    fun save(vararg items: T) {
        mDao.insertInTx(items.toList())
    }

    fun save(items: List<T>) {
        mDao.insertInTx(items)
    }

    fun saveOrUpdate(item: T): Long {
        return mDao.insertOrReplace(item)
    }

    fun saveOrUpdate(vararg items: T) {
        mDao.insertOrReplaceInTx(items.toList())
    }

    fun saveOrUpdate(items: List<T>) {
        mDao.insertOrReplaceInTx(items)
    }


    fun deleteByKey(key: K) {
        mDao.deleteByKey(key)
    }

    fun delete(item: T) {
        mDao.delete(item)
    }

    fun delete(vararg items: T) {
        mDao.deleteInTx(items.toList())
    }

    fun delete(items: List<T>) {
        mDao.deleteInTx(items)
    }

    fun deleteAll() {
        mDao.deleteAll()
    }


    fun update(item: T) {
        mDao.update(item)
    }

    fun update(vararg items: T) {
        mDao.updateInTx(items.toList())
    }

    fun update(items: List<T>) {
        mDao.updateInTx(items)
    }


    fun query(key: K): T {
        return mDao.load(key)
    }

    fun queryList():List<T>{
        return mDao.loadAll()
    }

    fun queryAll(): Single<List<T>> {
        return Single.create { emitter ->
            emitter.onSuccess(mDao.loadAll())
        }
    }

    fun query(where: String, vararg params: String): Single<List<T>> {
        return Single.create { emitter ->
            emitter.onSuccess(mDao.queryRaw(where, *params))
        }
    }

    fun count(): Long {
        return mDao.count()
    }


    fun refresh(item: T) {
        mDao.refresh(item)
    }

    fun detach(item: T) {
        mDao.detach(item)
    }
}
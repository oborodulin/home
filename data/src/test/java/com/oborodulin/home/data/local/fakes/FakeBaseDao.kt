package com.oborodulin.home.data.local.fakes

import com.oborodulin.home.data.local.db.dao.BaseDao
import com.oborodulin.home.data.local.db.entities.BaseEntity
import kotlinx.coroutines.delay
import java.util.*

open class FakeBaseDao<T : BaseEntity> : BaseDao<T> {
    protected var objs = HashMap<UUID, T>()

    override suspend fun insert(obj: T) {
        delay(1000)
        this.objs[obj.id()] = obj
    }

    override suspend fun insert(vararg obj: T) {
        delay(1000)
        obj.forEach { this.objs[it.id()] = it }
    }

    override suspend fun insert(objs: List<T>) {
        delay(1000)
        objs.forEach { this.objs[it.id()] = it }
    }

    override suspend fun update(obj: T) {
        delay(1000)
        this.objs[obj.id()] = obj
    }

    override suspend fun update(vararg obj: T) {
        delay(1000)
        obj.forEach { this.objs[it.id()] = it }
    }

    override suspend fun delete(obj: T) {
        delay(1000)
        this.objs.remove(obj.id())
    }

    override suspend fun delete(vararg obj: T) {
        delay(1000)
        obj.forEach { this.objs.remove(it.id()) }
    }

    override suspend fun delete(objs: List<T>) {
        delay(1000)
        objs.forEach { this.objs.remove(it.id()) }
    }
}
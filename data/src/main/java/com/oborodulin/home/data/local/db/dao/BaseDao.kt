package com.oborodulin.home.data.local.db.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import com.oborodulin.home.data.local.db.entities.BaseEntity

interface BaseDao<T : BaseEntity> {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(obj: T)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(vararg obj: T)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(objs: List<T>)

    @Update
    suspend fun update(obj: T)

    @Update
    suspend fun update(vararg obj: T)

    @Delete
    suspend fun delete(obj: T)

    @Delete
    suspend fun delete(vararg obj: T)

    @Delete
    suspend fun delete(objs: List<T>)
}
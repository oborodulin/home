package com.oborodulin.home.data.local.db.dao

import androidx.room.*
import com.oborodulin.home.data.local.db.entities.MeterEntity
import com.oborodulin.home.data.local.db.entities.PayerEntity
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface MeterDao {
    @Query("SELECT * FROM meters")
    fun getAll(): Flow<List<MeterEntity>>

    @Query("SELECT * FROM meters WHERE id=:id")
    fun get(id: UUID): Flow<MeterEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(meter: MeterEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAll(meters: List<MeterEntity>)

    @Update
    suspend fun update(meter: MeterEntity)

    @Delete
    suspend fun delete(meter: MeterEntity)

    @Query("DELETE FROM meters")
    fun deleteAll()
}
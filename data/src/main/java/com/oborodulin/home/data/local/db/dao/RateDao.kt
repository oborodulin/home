package com.oborodulin.home.data.local.db.dao

import androidx.room.*
import com.oborodulin.home.data.local.db.entities.RateEntity
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface RateDao {
    @Query("SELECT * FROM rates")
    fun getAll(): Flow<List<RateEntity>>

    @Query("SELECT * FROM rates WHERE rateId = :rateId")
    fun get(rateId: UUID): RateEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(rate: RateEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAll(rates: List<RateEntity>)

    @Update
    suspend fun update(rate: RateEntity)

    @Delete
    suspend fun delete(rate: RateEntity)

    @Delete
    suspend fun delete(rates: List<RateEntity>)

    @Query("DELETE FROM rates")
    fun deleteAll()
}
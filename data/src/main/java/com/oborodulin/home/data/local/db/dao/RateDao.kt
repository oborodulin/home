package com.oborodulin.home.data.local.db.dao

import androidx.room.*
import com.oborodulin.home.data.local.db.entities.RateEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import java.util.*

@Dao
interface RateDao {
    // READS:
    @Query("SELECT * FROM rates")
    fun findAll(): Flow<List<RateEntity>>

    @ExperimentalCoroutinesApi
    fun findAllDistinctUntilChanged() = findAll().distinctUntilChanged()

    @Query("SELECT * FROM rates WHERE rateId = :id")
    fun findById(id: UUID): Flow<RateEntity>

    @ExperimentalCoroutinesApi
    fun findByIdDistinctUntilChanged(id: UUID) = findById(id).distinctUntilChanged()

    // INSERTS:
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg rate: RateEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAll(rates: List<RateEntity>)

    // UPDATES:
    @Update
    suspend fun update(vararg rate: RateEntity)

    // DELETES:
    @Delete
    suspend fun delete(vararg rate: RateEntity)

    @Delete
    suspend fun delete(rates: List<RateEntity>)

    @Query("DELETE FROM rates")
    fun deleteAll()
}
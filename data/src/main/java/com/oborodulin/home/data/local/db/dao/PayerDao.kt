package com.oborodulin.home.data.local.db.dao

import androidx.room.*
import com.oborodulin.home.data.local.db.entities.PayerEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import java.util.*

@Dao
interface PayerDao {
    @Query("SELECT * FROM payers")
    fun _getAll(): Flow<List<PayerEntity>>

    @ExperimentalCoroutinesApi
    fun getAll() = _getAll().distinctUntilChanged()

    @Query("SELECT * FROM payers WHERE id=:id")
    fun _get(id: UUID): Flow<PayerEntity>

    @ExperimentalCoroutinesApi
    fun get(id: UUID) = _get(id).distinctUntilChanged()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(payer: PayerEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAll(payers: List<PayerEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(payer: PayerEntity)

    @Update
    suspend fun update(payer: PayerEntity)

    @Delete
    suspend fun delete(payer: PayerEntity)

    @Delete
    suspend fun delete(payers: List<PayerEntity>)

    @Query("DELETE FROM payers")
    suspend fun deleteAll()
}
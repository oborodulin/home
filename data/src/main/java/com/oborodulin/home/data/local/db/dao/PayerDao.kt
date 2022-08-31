package com.oborodulin.home.data.local.db.dao

//import androidx.lifecycle.LiveData
import androidx.room.*
import com.oborodulin.home.data.local.db.entities.PayerEntity
import java.util.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PayerDao {
    @Query("SELECT * FROM payers")
    fun getAll(): Flow<List<PayerEntity>>

    @Query("SELECT * FROM payers WHERE id=(:id)")
    suspend fun get(id: UUID): PayerEntity?

    @Insert
    suspend fun add(payer: PayerEntity)

    @Insert
    suspend fun addAll(payers: List<PayerEntity>)

    @Update
    suspend fun update(payer: PayerEntity)

    @Delete
    suspend fun delete(payer: PayerEntity)

    @Delete
    suspend fun delete(payers: List<PayerEntity>)

    @Query("DELETE FROM payers")
    suspend fun deleteAll()
}
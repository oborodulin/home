package com.oborodulin.home.domain.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.oborodulin.home.domain.entity.Payer
import java.util.*

@Dao
interface PayerDao {
    @Query("SELECT * FROM payers")
    suspend fun getAll(): List<Payer>

    @Query("SELECT * FROM payers WHERE id=(:id)")
    suspend fun get(id: UUID): Payer?

    @Insert
    suspend fun add(payer: Payer)

    @Update
    suspend fun update(payer: Payer)

    @Delete
    suspend fun delete(payer: Payer)

    @Query("DELETE FROM payers")
    suspend fun deleteAll()
}
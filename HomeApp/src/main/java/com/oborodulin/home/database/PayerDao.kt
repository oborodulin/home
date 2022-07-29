package com.oborodulin.home.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.oborodulin.home.domain.payer.Payer
import java.util.*

@Dao
interface PayerDao {
    @Query("SELECT * FROM payers")
    fun getAll(): LiveData<List<Payer>>

    @Query("SELECT * FROM payers WHERE id=(:id)")
    fun get(id: UUID): LiveData<Payer?>

    @Insert
    fun add(payer: Payer)

    @Update
    fun update(payer: Payer)

    @Delete
    fun delete(payer: Payer)

    @Query("DELETE FROM payers")
    fun deleteAll()
}
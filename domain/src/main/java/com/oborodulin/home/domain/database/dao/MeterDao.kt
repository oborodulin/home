package com.oborodulin.home.domain.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.oborodulin.home.domain.entity.Meter
import java.util.*

@Dao
interface MeterDao {
    @Query("SELECT * FROM meters")
    fun getAll(): LiveData<List<Meter>>

    @Query("SELECT * FROM meters WHERE id=(:id)")
    fun get(id: UUID): LiveData<Meter?>

    @Insert
    fun add(payer: Meter)

    @Update
    fun update(payer: Meter)

    @Delete
    fun delete(payer: Meter)

    @Query("DELETE FROM meters")
    fun deleteAll()
}
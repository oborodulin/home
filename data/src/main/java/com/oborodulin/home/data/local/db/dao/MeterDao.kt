package com.oborodulin.home.data.local.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.oborodulin.home.data.local.db.entities.Meter
import java.util.*

@Dao
interface MeterDao {
    @Query("SELECT * FROM meters")
    fun getAll(): LiveData<List<com.oborodulin.home.data.local.db.entities.Meter>>

    @Query("SELECT * FROM meters WHERE id=(:id)")
    fun get(id: UUID): LiveData<com.oborodulin.home.data.local.db.entities.Meter?>

    @Insert
    fun add(payer: com.oborodulin.home.data.local.db.entities.Meter)

    @Update
    fun update(payer: com.oborodulin.home.data.local.db.entities.Meter)

    @Delete
    fun delete(payer: com.oborodulin.home.data.local.db.entities.Meter)

    @Query("DELETE FROM meters")
    fun deleteAll()
}
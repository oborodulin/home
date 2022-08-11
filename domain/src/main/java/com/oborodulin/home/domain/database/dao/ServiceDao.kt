package com.oborodulin.home.domain.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.oborodulin.home.domain.entity.Service
import java.util.*

@Dao
interface ServiceDao {
    @Query("SELECT * FROM services ORDER BY displayPos")
    fun getAll(): LiveData<List<Service>>

    @Query("SELECT * FROM services WHERE id=:id")
    fun get(id: UUID): LiveData<Service?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(service: Service)

    @Update
    suspend fun update(service: Service)

    @Delete
    suspend fun delete(service: Service)

    @Query("DELETE FROM services")
    suspend fun deleteAll()

    @Query("SELECT IFNULL(MAX(displayPos), 0) FROM services")
    fun maxDisplayPos(): LiveData<Int>

    @Query("SELECT IFNULL(MAX(displayPos), 0) + 1 FROM services")
    fun nextDisplayPos(): LiveData<Int>

}
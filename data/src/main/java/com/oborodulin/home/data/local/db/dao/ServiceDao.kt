package com.oborodulin.home.data.local.db.dao

import androidx.room.*
import com.oborodulin.home.data.local.db.entities.ServiceEntity
import java.util.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ServiceDao {
    @Query("SELECT * FROM services ORDER BY pos")
    fun getAll(): Flow<List<ServiceEntity>>

    @Query("SELECT * FROM services WHERE id=:id")
    fun get(id: UUID): Flow<ServiceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(service: ServiceEntity)

    @Update
    suspend fun update(service: ServiceEntity)

    @Delete
    suspend fun delete(service: ServiceEntity)

    @Query("DELETE FROM services")
    suspend fun deleteAll()

    @Query("SELECT IFNULL(MAX(pos), 0) FROM services")
    fun maxDisplayPos(): Int

    @Query("SELECT IFNULL(MAX(pos), 0) + 1 FROM services")
    fun nextDisplayPos(): Int

}
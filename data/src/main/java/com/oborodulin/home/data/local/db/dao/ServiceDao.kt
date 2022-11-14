package com.oborodulin.home.data.local.db.dao

import androidx.room.*
import com.oborodulin.home.data.local.db.entities.ServiceEntity
import com.oborodulin.home.data.local.db.entities.ServiceTlEntity
import com.oborodulin.home.data.local.db.entities.pojo.ServicePojo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import java.util.*

@Dao
interface ServiceDao {
    @Query("SELECT * FROM services ORDER BY pos")
    fun getAll(): Flow<List<ServiceEntity>>

    @ExperimentalCoroutinesApi
    fun getAllDistinctUntilChanged() = getAll().distinctUntilChanged()

    @Query("SELECT * FROM services WHERE id=:id")
    fun get(id: UUID): Flow<ServiceEntity>

    @ExperimentalCoroutinesApi
    fun getDistinctUntilChanged(id: UUID) = get(id).distinctUntilChanged()

    @Query(
        "SELECT s.id, s.pos, s.type, stl.name, stl.measureUnit, stl.descr " +
                "FROM services AS s JOIN services_tl AS stl ON stl.servicesId = s.id " +
                "WHERE stl.localeCode = :locale ORDER BY s.pos"
    )
    fun getServices(locale: String? = Locale.getDefault().language): Flow<List<ServicePojo>>

    @ExperimentalCoroutinesApi
    fun getServicesDistinctUntilChanged() = getServices().distinctUntilChanged()

    @Query(
        "SELECT s.id, s.pos, s.type, stl.name, stl.measureUnit, stl.descr " +
                "FROM services AS s JOIN services_tl AS stl ON stl.servicesId = s.id " +
                "WHERE s.id = :id AND stl.localeCode = :locale ORDER BY s.pos"
    )
    fun getService(
        id: UUID,
        locale: String? = Locale.getDefault().language
    ): Flow<ServicePojo>

    @ExperimentalCoroutinesApi
    fun getServiceDistinctUntilChanged(id: UUID) = getService(id).distinctUntilChanged()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(textContent: ServiceTlEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(service: ServiceEntity)

    @Transaction
    suspend fun insert(service: ServiceEntity, textContent: ServiceTlEntity) {
        service.pos = service.pos ?: nextPos()
        updatePos(service.pos!!)
        insert(service)
        textContent.servicesId = service.id
        insert(textContent)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(service: ServiceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAll(services: List<ServiceEntity>)

    @Update
    suspend fun update(textContent: ServiceTlEntity)

    @Update
    suspend fun update(service: ServiceEntity)

    @Transaction
    suspend fun update(service: ServiceEntity, textContent: ServiceTlEntity) {
        service.pos = service.pos ?: nextPos()
        updatePos(service.pos!!)
        update(service)
        update(textContent)
    }

    @Delete
    suspend fun delete(service: ServiceEntity)

    @Query("DELETE FROM services")
    suspend fun deleteAll()

    @Query("SELECT IFNULL(MAX(pos), 0) FROM services")
    fun maxPos(): Int

    @Query("SELECT IFNULL(MAX(pos), 0) + 1 FROM services")
    fun nextPos(): Int

    @Query("UPDATE services SET pos = pos + 1 WHERE pos >= :pos")
    suspend fun updatePos(pos: Int)
}
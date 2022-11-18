package com.oborodulin.home.data.local.db.dao

import androidx.room.*
import com.oborodulin.home.data.local.db.entities.ServiceEntity
import com.oborodulin.home.data.local.db.entities.ServiceTlEntity
import com.oborodulin.home.data.local.db.views.ServiceView
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import java.util.*

@Dao
interface ServiceDao {
    // READS:
    @Query("SELECT * FROM services_view WHERE localeCode = :locale ORDER BY pos")
    fun findAll(locale: String? = Locale.getDefault().language): Flow<List<ServiceView>>

    @ExperimentalCoroutinesApi
    fun findAllDistinctUntilChanged() = findAll().distinctUntilChanged()

    @Query("SELECT * FROM services_view WHERE serviceId = :id AND localeCode = :locale")
    fun findById(id: UUID, locale: String? = Locale.getDefault().language): Flow<ServiceView>

    @ExperimentalCoroutinesApi
    fun findByIdDistinctUntilChanged(id: UUID) = findById(id).distinctUntilChanged()

    // INSERTS:
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg textContent: ServiceTlEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg service: ServiceEntity)

    @Transaction
    suspend fun insert(service: ServiceEntity, textContent: ServiceTlEntity) {
        service.pos = service.pos ?: nextPos()
        updatePos(service.pos!!)
        insert(service)
        textContent.servicesId = service.serviceId
        insert(textContent)
    }

    // UPDATES:
    @Update
    suspend fun update(vararg textContent: ServiceTlEntity)

    @Update
    suspend fun update(vararg service: ServiceEntity)

    @Transaction
    suspend fun update(service: ServiceEntity, textContent: ServiceTlEntity) {
        service.pos = service.pos ?: nextPos()
        updatePos(service.pos!!)
        update(service)
        update(textContent)
    }

    // DELETES:
    @Delete
    suspend fun delete(vararg service: ServiceEntity)

    @Query("DELETE FROM services")
    suspend fun deleteAll()

    // API:
    @Query("SELECT IFNULL(MAX(pos), 0) FROM services")
    fun maxPos(): Int

    @Query("SELECT IFNULL(MAX(pos), 0) + 1 FROM services")
    fun nextPos(): Int

    @Query("UPDATE services SET pos = pos + 1 WHERE pos >= :pos")
    suspend fun updatePos(pos: Int)
}
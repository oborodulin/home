package com.oborodulin.home.data.local.db.dao

import androidx.room.*
import com.oborodulin.home.data.local.db.entities.ServiceEntity
import com.oborodulin.home.data.local.db.entities.ServiceTlEntity
import com.oborodulin.home.data.local.db.entities.TranslateServiceEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import java.util.*

@Dao
interface ServiceDao {
    @Query("SELECT * FROM services ORDER BY pos")
    fun _getAll(): Flow<List<ServiceEntity>>

    @ExperimentalCoroutinesApi
    fun getAll() = _getAll().distinctUntilChanged()

    @Query("SELECT * FROM services WHERE id=:id")
    fun _get(id: UUID): Flow<ServiceEntity>

    @ExperimentalCoroutinesApi
    fun get(id: UUID) = _get(id).distinctUntilChanged()

    @Transaction
    @Query(
        "SELECT * FROM services JOIN services_tl ON services_tl.servicesId = services.id " +
                "JOIN languages ON languages.id = services_tl.languagesId " +
                "WHERE languages.localeCode = :locale ORDER BY services.pos"
    )
    fun _getAllContent(locale: String? = Locale.getDefault().language.toString()): Flow<List<TranslateServiceEntity>>

    @ExperimentalCoroutinesApi
    fun getAllContent() = _getAllContent().distinctUntilChanged()

    @Transaction
    @Query(
        "SELECT * FROM services JOIN services_tl ON services_tl.servicesId = services.id " +
                "JOIN languages ON languages.id = services_tl.languagesId " +
                "WHERE services.id = :id and languages.localeCode = :locale"
    )
    fun _getContent(
        id: UUID,
        locale: String? = Locale.getDefault().language.toString()
    ): Flow<TranslateServiceEntity>

    @ExperimentalCoroutinesApi
    fun getContent(id: UUID) = _getContent(id).distinctUntilChanged()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun _insert(textContent: ServiceTlEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun _insert(service: ServiceEntity)

    @Transaction
    suspend fun insert(service: ServiceEntity, textContent: ServiceTlEntity) {
        service.pos = service.pos ?: nextPos()
        updatePos(service.pos!!)
        _insert(service)
        textContent.servicesId = service.id
        _insert(textContent)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(service: ServiceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAll(services: List<ServiceEntity>)

    @Update
    suspend fun _update(textContent: ServiceTlEntity)

    @Update
    suspend fun _update(service: ServiceEntity)

    @Transaction
    suspend fun update(service: ServiceEntity, textContent: ServiceTlEntity) {
        service.pos = service.pos ?: nextPos()
        updatePos(service.pos!!)
        _update(service)
        _update(textContent)
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
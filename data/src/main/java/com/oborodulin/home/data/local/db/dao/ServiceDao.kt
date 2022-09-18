package com.oborodulin.home.data.local.db.dao

import androidx.room.*
import com.oborodulin.home.data.local.db.entities.ServiceEntity
import com.oborodulin.home.data.local.db.entities.ServiceTlEntity
import com.oborodulin.home.data.local.db.entities.pojo.PrevServiceMeterValuePojo
import com.oborodulin.home.data.local.db.entities.pojo.ServicePojo
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
    @Query("SELECT * FROM languages WHERE localeCode = :locale")
    fun _getAllContent(locale: String? = Locale.getDefault().language.toString()): Flow<List<ServicePojo>>

    @ExperimentalCoroutinesApi
    fun getAllContent() = _getAllContent().distinctUntilChanged()

    @Query(
        "SELECT services.*, services_tl.* FROM services JOIN services_tl ON services_tl.servicesId = services.id " +
                "JOIN languages ON languages.id = services_tl.languagesId " +
                "WHERE services.id = :id and languages.localeCode = :locale"
    )
    fun _getContent(
        id: UUID, locale: String? = Locale.getDefault().language.toString()
    ): Flow<ServicePojo>

    @ExperimentalCoroutinesApi
    fun getContent(id: UUID) = _getContent(id).distinctUntilChanged()

    @Query(
        "SELECT s.type, stl.name, IFNULL(mtl.measureUnit, stl.measureUnit) AS measureUnit " +
                "FROM payer_services AS ps JOIN services AS s ON ps.servicesId = s.Id " +
                "JOIN services_tl AS stl ON stl.servicesId = s.id " +
                "JOIN languages AS l ON l.id = stl.languagesId " +
                "JOIN meters m ON m.payerServicesId = ps.id " +
                "JOIN meters_tl AS mtl ON mtl.metersId = m.id " +
                "JOIN meter_values AS mvl ON mvl.metersId = m.id " +
                "WHERE ps.payersId = :payerId AND l.localeCode = :locale " +
                "AND mvl.valueDate = (SELECT max(mv.valueDate) FROM meter_values mv " +
                "WHERE  ) " +
                "ORDER BY s.pos "
    )
    fun _getPrevMetersValuesByPayer(
        payerId: UUID, locale: String? = Locale.getDefault().language.toString()
    ): Flow<List<PrevServiceMeterValuePojo>>

    @ExperimentalCoroutinesApi
    fun getPrevMetersValuesByPayer(payerId: UUID) =
        _getPrevMetersValuesByPayer(payerId).distinctUntilChanged()

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
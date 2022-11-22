package com.oborodulin.home.data.local.db.dao

import androidx.room.*
import com.oborodulin.home.data.local.db.entities.*
import com.oborodulin.home.data.local.db.entities.pojo.PrevServiceMeterValuePojo
import com.oborodulin.home.data.local.db.views.MeterView
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import java.util.*

@Dao
interface MeterDao {
    // READS:
    @Query("SELECT * FROM meters_view WHERE meters_view.localeCode = :locale")
    fun findAll(locale: String? = Locale.getDefault().language): Flow<List<MeterView>>

    @ExperimentalCoroutinesApi
    fun findAllDistinctUntilChanged() = findAll().distinctUntilChanged()

    @Query("SELECT mv.* FROM meters_view AS mv WHERE mv.meterId = :meterId AND mv.localeCode = :locale")
    fun findById(meterId: UUID, locale: String? = Locale.getDefault().language): Flow<MeterView>

    @ExperimentalCoroutinesApi
    fun findByIdDistinctUntilChanged(id: UUID) = findById(id).distinctUntilChanged()

    @Query(
        "SELECT mv.* FROM meters_view AS mv WHERE mv.payersId = :payerId AND mv.localeCode = :locale"
    )
    fun findByPayerId(payerId: UUID, locale: String? = Locale.getDefault().language):
            Flow<List<MeterView>>

    @ExperimentalCoroutinesApi
    fun findByPayerIdDistinctUntilChanged(payerId: UUID) =
        findByPayerId(payerId).distinctUntilChanged()

    @Query(
        "SELECT mv.* FROM meters_view AS mv WHERE mv.servicesId = :serviceId AND mv.localeCode = :locale"
    )
    fun findByServiceId(serviceId: UUID, locale: String? = Locale.getDefault().language):
            Flow<List<MeterView>>

    @ExperimentalCoroutinesApi
    fun findByServiceIdDistinctUntilChanged(serviceId: UUID) =
        findByPayerId(serviceId).distinctUntilChanged()

    @Query(
        "SELECT mvl.meterValueId, mv.payersId AS payerId, mv.servicesId AS serviceId, sv.type, " +
                "sv.name, mv.meterId, IFNULL(mv.measureUnit, sv.measureUnit) AS measureUnit, " +
                "mvl.valueDate AS prevLastDate, mvl.meterValue AS prevValue " +
                "FROM meters_view AS mv JOIN services_view AS sv ON sv.serviceId = mv.servicesId " +
                "JOIN meter_values AS mvl ON mvl.metersId = mv.meterId " +
                "JOIN (SELECT v.metersId, MAX(v.valueDate) maxValueDate " +
                "FROM meter_values v JOIN meters m ON m.meterId = v.metersId " +
                "JOIN payers_services AS ps ON ps.payerServiceId = m.payersServicesId " +
                "JOIN payers AS p ON p.payerId = ps.payersId " +
                "WHERE v.valueDate <= IIF(strftime('%s', 'now') > strftime('%s', 'now', 'start of month', 'start of month', '+' || IFNULL(p.paymentDay, 20) || ' days')," +
                "strftime('%s', 'now', 'start of month', '+' || IFNULL(p.paymentDay, 20) || ' days')," +
                "strftime('%s', 'now', '-1 months', 'start of month', '+' || IFNULL(p.paymentDay, 20) || ' days')) * 1000 " +
                "GROUP BY v.metersId) mp ON mp.metersId = mvl.metersId AND mp.maxValueDate = mvl.valueDate " +
                "WHERE mv.payersId = :payerId AND mv.localeCode = :locale AND sv.localeCode = :locale " +
                "ORDER BY sv.pos"
    )
    fun findPrevMetersValuesByPayerId(
        payerId: UUID, locale: String? = Locale.getDefault().language
    ): Flow<List<PrevServiceMeterValuePojo>>

    @ExperimentalCoroutinesApi
    fun findPrevMetersValuesByPayerIdDistinctUntilChanged(payerId: UUID) =
        findPrevMetersValuesByPayerId(payerId).distinctUntilChanged()

    @Query("SELECT * FROM meter_values WHERE metersId = :meterId")
    fun findValuesByMeterId(meterId: UUID): Flow<List<MeterValueEntity>>

    @ExperimentalCoroutinesApi
    fun findValuesByMeterIdDistinctUntilChanged(meterId: UUID) =
        findValuesByMeterId(meterId).distinctUntilChanged()

    @Query("SELECT * FROM meter_verifications WHERE metersId = :meterId")
    fun findVerificationsByMeterId(meterId: UUID): Flow<List<MeterVerificationEntity>>

    @ExperimentalCoroutinesApi
    fun findVerificationsByMeterIdDistinctUntilChanged(meterId: UUID) =
        findValuesByMeterId(meterId).distinctUntilChanged()

    // INSERTS:
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg meter: MeterEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg textContent: MeterTlEntity)

    @Transaction
    suspend fun insert(meter: MeterEntity, textContent: MeterTlEntity) {
        insert(meter)
        textContent.metersId = meter.meterId
        insert(textContent)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg meterValues: MeterValueEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg meterVerifications: MeterVerificationEntity)

    // UPDATES:
    @Update
    suspend fun update(vararg textContent: MeterTlEntity)

    @Update
    suspend fun update(vararg meter: MeterEntity)

    @Transaction
    suspend fun update(meter: MeterEntity, textContent: MeterTlEntity) {
        update(meter)
        update(textContent)
    }

    // DELETES:
    @Delete
    suspend fun delete(vararg meter: MeterEntity)

    @Delete
    suspend fun delete(meters: List<MeterEntity>)

    @Query("DELETE FROM meters")
    suspend fun deleteAll()
}
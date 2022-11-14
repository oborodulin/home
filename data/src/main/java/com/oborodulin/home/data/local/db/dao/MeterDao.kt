package com.oborodulin.home.data.local.db.dao

import androidx.room.*
import com.oborodulin.home.data.local.db.entities.*
import com.oborodulin.home.data.local.db.entities.pojo.MeterPojo
import com.oborodulin.home.data.local.db.entities.pojo.PrevServiceMeterValuePojo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import java.util.*

@Dao
interface MeterDao {
    @Query(
        "SELECT m.* FROM meters AS m " +
                "JOIN payer_services AS ps ON ps.id = m.payerServicesId " +
                "JOIN services AS s ON s.Id = ps.servicesId " +
                "ORDER BY s.pos"
    )
    fun getAll(): Flow<List<MeterEntity>>

    @ExperimentalCoroutinesApi
    fun getAllDistinctUntilChanged() = getAll().distinctUntilChanged()

    @Query("SELECT * FROM meters WHERE id=:id")
    fun get(id: UUID): Flow<MeterEntity>

    @ExperimentalCoroutinesApi
    fun getDistinctUntilChanged(id: UUID) = get(id).distinctUntilChanged()

    @Query(
        "SELECT m.id, m.payerServicesId, m.num, m.maxValue, m.passportDate, m.verificationPeriod, " +
                "mtl.id AS metersTlId, mtl.measureUnit, mtl.descr " +
                "FROM meters AS m JOIN meters_tl AS mtl ON mtl.metersId = m.id " +
                "JOIN payer_services AS ps ON ps.id = m.payerServicesId " +
                "JOIN services AS s ON s.Id = ps.servicesId " +
                "WHERE mtl.localeCode = :locale ORDER BY s.pos"
    )
    fun getMeters(locale: String? = Locale.getDefault().language): Flow<List<MeterPojo>>

    @ExperimentalCoroutinesApi
    fun getMetersDistinctUntilChanged() = getMeters().distinctUntilChanged()

    @Query(
        "SELECT m.id, m.payerServicesId, m.num, m.maxValue, m.passportDate, m.verificationPeriod, " +
                "mtl.id AS metersTlId, mtl.measureUnit, mtl.descr " +
                "FROM meters AS m JOIN meters_tl AS mtl ON mtl.metersId = m.id " +
                "WHERE m.id = :id AND mtl.localeCode = :locale"
    )
    fun getMeter(id: UUID, locale: String? = Locale.getDefault().language): Flow<MeterPojo>

    @ExperimentalCoroutinesApi
    fun getMeterDistinctUntilChanged(id: UUID) = getMeter(id).distinctUntilChanged()

    @Query(
        "SELECT m.id, m.payerServicesId, m.num, m.maxValue, m.passportDate, m.verificationPeriod, " +
                "mtl.id AS metersTlId, mtl.measureUnit, mtl.descr " +
                "FROM meters AS m JOIN meters_tl AS mtl ON mtl.metersId = m.id " +
                "JOIN payer_services AS ps ON ps.id = m.payerServicesId " +
                "JOIN services AS s ON s.Id = ps.servicesId " +
                "WHERE ps.payersId = :payerId AND mtl.localeCode = :locale ORDER BY s.pos"
    )
    fun getMeters(
        payerId: UUID,
        locale: String? = Locale.getDefault().language
    ): Flow<List<MeterPojo>>

    @ExperimentalCoroutinesApi
    fun getMetersDistinctUntilChanged(payerId: UUID) = getMeters(payerId).distinctUntilChanged()

    @Query(
        "SELECT ps.payersId AS payerId, s.id AS serviceId, s.type, stl.name, m.id AS meterId, " +
                "IFNULL(mtl.measureUnit, stl.measureUnit) AS measureUnit, " +
                "mvl.valueDate AS prevLastDate, mvl.meterValue AS prevValue " +
                "FROM payer_services AS ps JOIN services AS s ON ps.servicesId = s.Id " +
                "JOIN services_tl AS stl ON stl.servicesId = s.id " +
                "JOIN meters m ON m.payerServicesId = ps.id " +
                "JOIN meters_tl AS mtl ON mtl.metersId = m.id " +
                "JOIN meter_values AS mvl ON mvl.metersId = m.id " +
                "JOIN (SELECT v.metersId, MAX(v.valueDate) maxValueDate " +
                "FROM meter_values v JOIN meters m ON m.id = v.metersId " +
                "JOIN payer_services AS ps ON ps.id = m.payerServicesId " +
                "JOIN payers AS p ON p.id = ps.payersId " +
                "WHERE v.valueDate <= IIF(strftime('%s', 'now') > strftime('%s', 'now', 'start of month', 'start of month', '+' || IFNULL(p.paymentDay, 20) || ' days')," +
                "strftime('%s', 'now', 'start of month', '+' || IFNULL(p.paymentDay, 20) || ' days')," +
                "strftime('%s', 'now', '-1 months', 'start of month', '+' || IFNULL(p.paymentDay, 20) || ' days')) * 1000 " +
                "GROUP BY v.metersId) mv ON mvl.metersId = mv.metersId AND mvl.valueDate = mv.maxValueDate " +
                "WHERE ps.payersId = :payerId AND stl.localeCode = :locale AND mtl.localeCode = :locale " +
                "ORDER BY s.pos"
    )
    fun getPrevMetersValuesByPayer(
        payerId: UUID, locale: String? = Locale.getDefault().language
    ): Flow<List<PrevServiceMeterValuePojo>>

    @ExperimentalCoroutinesApi
    fun getPrevMetersValuesByPayerDistinctUntilChanged(payerId: UUID) =
        getPrevMetersValuesByPayer(payerId).distinctUntilChanged()

    @Query(
        "SELECT * FROM " +
                "(SELECT meters.* FROM meters JOIN payer_services AS ps ON ps.id = meters.payerServicesId " +
                "WHERE ps.payersId = :payerId) m " +
                "JOIN meter_values ON meter_values.metersId = m.id"
    )
    fun getMeterAndValues(payerId: UUID): Flow<Map<MeterEntity, List<MeterValueEntity>>>

    @ExperimentalCoroutinesApi
    fun getMeterAndValuesDistinctUntilChanged(payerId: UUID) =
        getMeterAndValues(payerId).distinctUntilChanged()

    @Query(
        "SELECT * FROM " +
                "(SELECT meters.* FROM meters JOIN payer_services AS ps ON ps.id = meters.payerServicesId " +
                "WHERE ps.payersId = :payerId) m " +
                "JOIN meter_verifications ON meter_verifications.metersId = m.id"
    )
    fun getMeterAndVerifications(payerId: UUID): Flow<Map<MeterEntity, List<MeterVerificationEntity>>>

    @ExperimentalCoroutinesApi
    fun getMeterAndVerificationsDistinctUntilChanged(payerId: UUID) =
        getMeterAndValues(payerId).distinctUntilChanged()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(textContent: MeterTlEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(meter: MeterEntity)

    @Transaction
    suspend fun insert(meter: MeterEntity, textContent: MeterTlEntity) {
        insert(meter)
        textContent.metersId = meter.id
        insert(textContent)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(meter: MeterEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAll(meter: List<MeterEntity>)

    @Update
    suspend fun update(textContent: MeterTlEntity)

    @Update
    suspend fun update(meter: MeterEntity)

    @Transaction
    suspend fun update(meter: MeterEntity, textContent: MeterTlEntity) {
        update(meter)
        update(textContent)
    }

    @Delete
    suspend fun delete(meter: MeterEntity)

    @Delete
    suspend fun delete(meters: List<MeterEntity>)

    @Query("DELETE FROM meters")
    suspend fun deleteAll()
}
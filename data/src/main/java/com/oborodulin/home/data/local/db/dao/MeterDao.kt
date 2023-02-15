package com.oborodulin.home.data.local.db.dao

import androidx.room.*
import com.oborodulin.home.data.local.db.entities.*
import com.oborodulin.home.data.local.db.views.MeterValuePrevPeriodsView
import com.oborodulin.home.data.local.db.views.MetersView
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import java.util.*

@Dao
interface MeterDao {
    // READS:
    @Query("SELECT * FROM meters_view WHERE meters_view.localeCode = :locale")
    fun findAll(locale: String? = Locale.getDefault().language): Flow<List<MetersView>>

    @ExperimentalCoroutinesApi
    fun findAllDistinctUntilChanged() = findAll().distinctUntilChanged()

    @Query("SELECT mv.* FROM meters_view AS mv WHERE mv.meterId = :meterId AND mv.localeCode = :locale")
    fun findById(meterId: UUID, locale: String? = Locale.getDefault().language): Flow<MetersView>

    @ExperimentalCoroutinesApi
    fun findByIdDistinctUntilChanged(id: UUID) = findById(id).distinctUntilChanged()

    @Query(
        "SELECT mv.* FROM meters_view AS mv WHERE mv.payersId = :payerId AND mv.localeCode = :locale"
    )
    fun findByPayerId(payerId: UUID, locale: String? = Locale.getDefault().language):
            Flow<List<MetersView>>

    @ExperimentalCoroutinesApi
    fun findByPayerIdDistinctUntilChanged(payerId: UUID) =
        findByPayerId(payerId).distinctUntilChanged()

    @Query(
        "SELECT mv.* FROM meters_view AS mv WHERE mv.servicesId = :serviceId AND mv.localeCode = :locale"
    )
    fun findByServiceId(serviceId: UUID, locale: String? = Locale.getDefault().language):
            Flow<List<MetersView>>

    @ExperimentalCoroutinesApi
    fun findByServiceIdDistinctUntilChanged(serviceId: UUID) =
        findByPayerId(serviceId).distinctUntilChanged()

    @Query(
        "SELECT pmv.* FROM meter_value_prev_periods_view pmv " +
                "WHERE pmv.payerId = :payerId AND pmv.meterLocaleCode = :locale AND pmv.serviceLocaleCode = :locale " +
                "ORDER BY pmv.pos"
    )
    fun findPrevMetersValuesByPayerId(
        payerId: UUID, locale: String? = Locale.getDefault().language
    ): Flow<List<MeterValuePrevPeriodsView>>

    @Query(
        "SELECT pmv.* FROM meter_value_prev_periods_view pmv " +
                "WHERE pmv.isFavorite = 1 AND pmv.meterLocaleCode = :locale AND pmv.serviceLocaleCode = :locale " +
                "ORDER BY pmv.pos"
    )
    fun findPrevMetersValuesByPayerIsFavorite(
        locale: String? = Locale.getDefault().language
    ): Flow<List<MeterValuePrevPeriodsView>>

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

    @Update
    suspend fun update(vararg meterValues: MeterValueEntity)

    @Update
    suspend fun update(vararg meterVerifications: MeterVerificationEntity)

    // DELETES:
    @Delete
    suspend fun delete(vararg meter: MeterEntity)

    @Delete
    suspend fun delete(meters: List<MeterEntity>)

    @Query("DELETE FROM meters")
    suspend fun deleteAll()

    @Query(
        """
        DELETE FROM meter_values WHERE metersId = :meterId AND datetime(valueDate) = 
                (SELECT MAX(datetime(v.valueDate)) FROM meter_values v 
                WHERE v.metersId = :meterId 
                AND datetime(v.valueDate) > (SELECT mpd.maxValueDate 
                                               FROM meter_value_max_prev_dates_view mpd 
                                           WHERE mpd.meterId = :meterId))
"""
    )
    suspend fun deleteCurrentValue(meterId: UUID)
}
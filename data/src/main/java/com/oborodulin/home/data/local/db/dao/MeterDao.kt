package com.oborodulin.home.data.local.db.dao

import androidx.room.*
import com.oborodulin.home.data.local.db.entities.*
import com.oborodulin.home.data.local.db.views.MeterPayerServiceView
import com.oborodulin.home.data.local.db.views.MeterValuePrevPeriodView
import com.oborodulin.home.data.local.db.views.MeterView
import com.oborodulin.home.data.util.Constants
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import java.util.*

@Dao
interface MeterDao : BaseDao<MeterEntity> {
    // READS:
    @Query("SELECT * FROM ${MeterView.VIEW_NAME} WHERE meterLocCode = :locale")
    fun findAll(locale: String? = Locale.getDefault().language): Flow<List<MeterView>>

    @ExperimentalCoroutinesApi
    fun findDistinctAll() = findAll().distinctUntilChanged()

    @Query("SELECT * FROM ${MeterView.VIEW_NAME} WHERE meterId = :meterId AND meterLocCode = :locale")
    fun findById(meterId: UUID, locale: String? = Locale.getDefault().language): Flow<MeterView>

    @ExperimentalCoroutinesApi
    fun findDistinctById(id: UUID) = findById(id).distinctUntilChanged()

    @Query(
        "SELECT * FROM ${MeterView.VIEW_NAME} WHERE payersId = :payerId AND meterLocCode = :locale"
    )
    fun findByPayerId(payerId: UUID, locale: String? = Locale.getDefault().language):
            Flow<List<MeterView>>

    @ExperimentalCoroutinesApi
    fun findDistinctByPayerId(payerId: UUID) = findByPayerId(payerId).distinctUntilChanged()

    @Query(
        "SELECT * FROM ${MeterPayerServiceView.VIEW_NAME} WHERE serviceId = :serviceId AND serviceLocCode = :locale"
    )
    fun findByServiceId(serviceId: UUID, locale: String? = Locale.getDefault().language):
            Flow<List<MeterPayerServiceView>>

    @ExperimentalCoroutinesApi
    fun findDistinctByServiceId(serviceId: UUID) = findByServiceId(serviceId).distinctUntilChanged()

    @Query(
        """
SELECT * FROM ${MeterValuePrevPeriodView.VIEW_NAME}  
WHERE payerId = :payerId AND meterLocCode = :locale AND serviceLocCode = :locale 
ORDER BY servicePos
"""
    )
    fun findPrevMetersValuesByPayerId(
        payerId: UUID, locale: String? = Locale.getDefault().language
    ): Flow<List<MeterValuePrevPeriodView>>

    @Query(
        """
SELECT * FROM ${MeterValuePrevPeriodView.VIEW_NAME} 
WHERE isFavorite = 1 AND meterLocCode = :locale AND serviceLocCode = :locale
ORDER BY servicePos
"""
    )
    fun findPrevMetersValuesByFavoritePayer(
        locale: String? = Locale.getDefault().language
    ): Flow<List<MeterValuePrevPeriodView>>

    @ExperimentalCoroutinesApi
    fun findDistinctPrevMetersValuesByPayerId(payerId: UUID) =
        findPrevMetersValuesByPayerId(payerId).distinctUntilChanged()

    @Query("SELECT * FROM meter_values WHERE metersId = :meterId")
    fun findValuesByMeterId(meterId: UUID): Flow<List<MeterValueEntity>>

    @ExperimentalCoroutinesApi
    fun findDistinctValuesByMeterId(meterId: UUID) =
        findValuesByMeterId(meterId).distinctUntilChanged()

    @Query("SELECT * FROM meter_verifications WHERE metersId = :meterId")
    fun findVerificationsByMeterId(meterId: UUID): Flow<List<MeterVerificationEntity>>

    @ExperimentalCoroutinesApi
    fun findDistinctVerificationsByMeterId(meterId: UUID) =
        findValuesByMeterId(meterId).distinctUntilChanged()

    // INSERTS:
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg textContent: MeterTlEntity)

    @Transaction
    suspend fun insert(meter: MeterEntity, textContent: MeterTlEntity) {
        insert(meter)
        insert(textContent)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg meterValues: MeterValueEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg meterVerifications: MeterVerificationEntity)

    // UPDATES:
    @Update
    suspend fun update(vararg textContent: MeterTlEntity)

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
    @Query("DELETE FROM meters")
    suspend fun deleteAll()

    @Query(
        """
        DELETE FROM meter_values WHERE metersId = :meterId 
            AND strftime(${Constants.DB_FRACT_SEC_TIME}, valueDate) = 
                (SELECT MAX(strftime(${Constants.DB_FRACT_SEC_TIME}, v.valueDate)) FROM meter_values v 
                WHERE v.metersId = :meterId 
                AND strftime(${Constants.DB_FRACT_SEC_TIME}, v.valueDate) > 
                                        (SELECT mpd.maxValueDate 
                                               FROM meter_value_max_prev_dates_view mpd 
                                           WHERE mpd.meterId = :meterId))
"""
    )
    suspend fun deleteCurrentValue(meterId: UUID)
}
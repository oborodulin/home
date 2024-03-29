package com.oborodulin.home.data.local.db.dao

import androidx.room.*
import com.oborodulin.home.data.local.db.entities.PayerServiceCrossRefEntity
import com.oborodulin.home.data.local.db.entities.RateEntity
import com.oborodulin.home.data.local.db.views.PayerServiceDebtView
import com.oborodulin.home.data.local.db.views.PayerServiceSubtotalDebtView
import com.oborodulin.home.data.local.db.views.PayerTotalDebtView
import com.oborodulin.home.data.local.db.views.RatePayerServiceView
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import java.util.*

@Dao
interface RateDao { //  : BaseDao<RateEntity>
    // READS:
    @Query("SELECT * FROM ${RateEntity.TABLE_NAME}")
    fun findAll(): Flow<List<RateEntity>>

    @ExperimentalCoroutinesApi
    fun findDistinctAll() = findAll().distinctUntilChanged()

    @Query("SELECT * FROM ${RateEntity.TABLE_NAME} WHERE rateId = :id")
    fun findById(id: UUID): Flow<RateEntity>

    @ExperimentalCoroutinesApi
    fun findDistinctById(id: UUID) = findById(id).distinctUntilChanged()

    @Query(
        """
SELECT r.* FROM ${RateEntity.TABLE_NAME} r JOIN ${PayerServiceCrossRefEntity.TABLE_NAME} ps 
            ON ps.payerServiceId = r.payersServicesId AND ps.payersId = :payerId
"""
    )
    fun findByPayerId(payerId: UUID): Flow<List<RateEntity>>

    @ExperimentalCoroutinesApi
    fun findDistinctByPayerId(payerId: UUID) = findByPayerId(payerId).distinctUntilChanged()

    @Query("SELECT * FROM ${RateEntity.TABLE_NAME} WHERE servicesId = :serviceId AND payersServicesId IS NULL")
    fun findByServiceId(serviceId: UUID): Flow<List<RateEntity>>

    @ExperimentalCoroutinesApi
    fun findDistinctByServiceId(serviceId: UUID) = findByServiceId(serviceId).distinctUntilChanged()

    @Query("SELECT * FROM ${RateEntity.TABLE_NAME} WHERE payersServicesId = :payerServiceId")
    fun findByPayerServiceId(payerServiceId: UUID): Flow<List<RateEntity>>

    @ExperimentalCoroutinesApi
    fun findDistinctByPayerServiceId(payerServiceId: UUID) =
        findByPayerServiceId(payerServiceId).distinctUntilChanged()

    @Query("SELECT * FROM ${RatePayerServiceView.VIEW_NAME} WHERE payerId = :payerId AND serviceLocCode = :locale ORDER BY servicePos")
    fun findRatesByPayerServices(payerId: UUID, locale: String? = Locale.getDefault().language):
            Flow<List<RatePayerServiceView>>

    @Query(
        """
SELECT * FROM ${PayerServiceDebtView.VIEW_NAME} WHERE payerId = :payerId AND serviceLocCode = :locale
ORDER BY servicePos, fromPaymentDate, startMeterValue            
"""
    )
    fun findServiceDebtsByPayerId(
        payerId: UUID, locale: String? = Locale.getDefault().language
    ): Flow<List<PayerServiceDebtView>>

    @Query(
        """
SELECT * FROM ${PayerServiceSubtotalDebtView.VIEW_NAME} WHERE payerId = :payerId AND serviceLocCode = :locale
ORDER BY servicePos            
"""
    )
    fun findSubtotalDebtsByPayerId(
        payerId: UUID, locale: String? = Locale.getDefault().language
    ): Flow<List<PayerServiceSubtotalDebtView>>

    @Query(
        "SELECT * FROM ${PayerTotalDebtView.VIEW_NAME} WHERE serviceLocCode = :locale"
    )
    fun findTotalDebts(locale: String? = Locale.getDefault().language): Flow<List<PayerTotalDebtView>>

    @Query(
        "SELECT * FROM ${PayerTotalDebtView.VIEW_NAME} WHERE payerId = :payerId AND serviceLocCode = :locale"
    )
    fun findTotalDebtByPayerId(
        payerId: UUID, locale: String? = Locale.getDefault().language
    ): Flow<PayerTotalDebtView>

    // INSERTS:
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(rate: RateEntity)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(vararg rates: RateEntity)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(rates: List<RateEntity>)

    // UPDATES:
    @Update
    suspend fun update(rate: RateEntity)

    @Update
    suspend fun update(vararg rates: RateEntity)

    // DELETES:
    @Delete
    suspend fun delete(rate: RateEntity)

    @Delete
    suspend fun delete(vararg rates: RateEntity)

    @Delete
    suspend fun delete(rates: List<RateEntity>)

    @Query("DELETE FROM ${RateEntity.TABLE_NAME} WHERE rateId = :rateId")
    suspend fun deleteById(rateId: UUID)

    @Query("DELETE FROM ${RateEntity.TABLE_NAME}")
    suspend fun deleteAll()
}
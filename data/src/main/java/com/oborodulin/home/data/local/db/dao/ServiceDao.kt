package com.oborodulin.home.data.local.db.dao

import androidx.room.*
import com.oborodulin.home.data.local.db.entities.ServiceEntity
import com.oborodulin.home.data.local.db.entities.ServiceTlEntity
import com.oborodulin.home.data.local.db.views.PayerServiceSubtotalDebtView
import com.oborodulin.home.data.local.db.views.PayerServiceView
import com.oborodulin.home.data.local.db.views.PayerTotalDebtView
import com.oborodulin.home.data.local.db.views.ServiceView
import com.oborodulin.home.data.util.Constants
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import java.util.*

@Dao
interface ServiceDao : BaseDao<ServiceEntity> {
    // READS:
    @Query("SELECT * FROM ${ServiceView.VIEW_NAME} WHERE localeCode = :locale ORDER BY pos")
    fun findAll(locale: String? = Locale.getDefault().language): Flow<List<ServiceView>>

    @ExperimentalCoroutinesApi
    fun findDistinctAll() = findAll().distinctUntilChanged()

    @Query("SELECT * FROM ${ServiceView.VIEW_NAME} WHERE serviceId = :id AND localeCode = :locale")
    fun findById(id: UUID, locale: String? = Locale.getDefault().language): Flow<ServiceView>

    @ExperimentalCoroutinesApi
    fun findDistinctById(id: UUID) = findById(id).distinctUntilChanged()

    @Query("SELECT * FROM ${ServiceView.VIEW_NAME} WHERE meterType <> ${Constants.MTR_NONE_VAL} AND localeCode = :locale ORDER BY pos")
    fun findMeterAllowed(locale: String? = Locale.getDefault().language): Flow<List<ServiceView>>

    @Query(
        "SELECT * FROM ${PayerServiceView.VIEW_NAME} WHERE payersId = :payerId AND localeCode = :locale"
    )
    fun findByPayerId(payerId: UUID, locale: String? = Locale.getDefault().language):
            Flow<List<PayerServiceView>>

    @ExperimentalCoroutinesApi
    fun findDistinctByPayerId(payerId: UUID) = findByPayerId(payerId).distinctUntilChanged()

    @Query(
        "SELECT * FROM ${PayerServiceView.VIEW_NAME} WHERE payerServiceId = :payerServiceId AND localeCode = :locale"
    )
    fun findPayerServiceById(payerServiceId: UUID, locale: String? = Locale.getDefault().language):
            Flow<PayerServiceView>

    @ExperimentalCoroutinesApi
    fun findDistinctPayerServiceById(payerServiceId: UUID) =
        findPayerServiceById(payerServiceId).distinctUntilChanged()

    @Query(
        """
SELECT * FROM ${PayerServiceSubtotalDebtView.VIEW_NAME} WHERE payerId = :payerId AND serviceLocaleCode = :locale
ORDER BY pos            
"""
    )
    fun findSubtotalDebtsByPayerId(
        payerId: UUID, locale: String? = Locale.getDefault().language
    ): Flow<List<PayerServiceSubtotalDebtView>>

    @Query(
        "SELECT * FROM ${PayerTotalDebtView.VIEW_NAME} WHERE serviceLocaleCode = :locale"
    )
    fun findTotalDebts(locale: String? = Locale.getDefault().language): Flow<List<PayerTotalDebtView>>

    @Query(
        "SELECT * FROM payer_total_debts_view WHERE payerId = :payerId AND serviceLocaleCode = :locale"
    )
    fun findTotalDebtByPayerId(
        payerId: UUID, locale: String? = Locale.getDefault().language
    ): Flow<PayerTotalDebtView>

    // INSERTS:
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg textContent: ServiceTlEntity)

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

    @Transaction
    suspend fun update(service: ServiceEntity, textContent: ServiceTlEntity) {
        service.pos = service.pos ?: nextPos()
        updatePos(service.pos!!)
        update(service)
        update(textContent)
    }

    // DELETES:
    @Query("DELETE FROM ${ServiceEntity.TABLE_NAME} WHERE serviceId = :serviceId")
    suspend fun deleteById(serviceId: UUID)

    @Query("DELETE FROM ${ServiceEntity.TABLE_NAME}")
    suspend fun deleteAll()

    // API:
    @Query("SELECT IFNULL(MAX(pos), 0) FROM ${ServiceEntity.TABLE_NAME}")
    fun maxPos(): Int

    @Query("SELECT IFNULL(MAX(pos), 0) + 1 FROM ${ServiceEntity.TABLE_NAME}")
    fun nextPos(): Int

    @Query("UPDATE ${ServiceEntity.TABLE_NAME} SET pos = pos + 1 WHERE pos >= :pos")
    suspend fun updatePos(pos: Int)
}
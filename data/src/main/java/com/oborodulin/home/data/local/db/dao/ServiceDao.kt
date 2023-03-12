package com.oborodulin.home.data.local.db.dao

import androidx.room.*
import com.oborodulin.home.data.local.db.entities.PayerServiceCrossRefEntity
import com.oborodulin.home.data.local.db.entities.ServiceEntity
import com.oborodulin.home.data.local.db.entities.ServiceTlEntity
import com.oborodulin.home.data.local.db.views.MeterPayerServiceView
import com.oborodulin.home.data.local.db.views.PayerServiceView
import com.oborodulin.home.data.local.db.views.ServiceView
import com.oborodulin.home.data.util.Constants
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import java.util.*

@Dao
interface ServiceDao : BaseDao<ServiceEntity> {
    // READS:
    @Query("SELECT * FROM ${ServiceView.VIEW_NAME} WHERE serviceLocCode = :locale ORDER BY servicePos")
    fun findAll(locale: String? = Locale.getDefault().language): Flow<List<ServiceView>>

    @ExperimentalCoroutinesApi
    fun findDistinctAll() = findAll().distinctUntilChanged()

    @Query("SELECT * FROM ${ServiceView.VIEW_NAME} WHERE serviceId = :id AND serviceLocCode = :locale")
    fun findById(id: UUID, locale: String? = Locale.getDefault().language): Flow<ServiceView>

    @ExperimentalCoroutinesApi
    fun findDistinctById(id: UUID) = findById(id).distinctUntilChanged()

    @Query("SELECT * FROM ${ServiceView.VIEW_NAME} WHERE serviceMeterType <> ${Constants.MTR_NONE_VAL} AND serviceLocCode = :locale ORDER BY servicePos")
    fun findMeterAllowed(locale: String? = Locale.getDefault().language): Flow<List<ServiceView>>

    @Query(
        "SELECT * FROM ${PayerServiceView.VIEW_NAME} WHERE payersId = :payerId AND serviceLocCode = :locale ORDER BY servicePos"
    )
    fun findByPayerId(payerId: UUID, locale: String? = Locale.getDefault().language):
            Flow<List<PayerServiceView>>

    @ExperimentalCoroutinesApi
    fun findDistinctByPayerId(payerId: UUID) = findByPayerId(payerId).distinctUntilChanged()

    @Query(
        "SELECT * FROM ${PayerServiceView.VIEW_NAME} WHERE payerServiceId = :payerServiceId AND serviceLocCode = :locale"
    )
    fun findPayerServiceById(payerServiceId: UUID, locale: String? = Locale.getDefault().language):
            Flow<PayerServiceView>

    @ExperimentalCoroutinesApi
    fun findDistinctPayerServiceById(payerServiceId: UUID) =
        findPayerServiceById(payerServiceId).distinctUntilChanged()

    @Query(
        "SELECT * FROM ${MeterPayerServiceView.VIEW_NAME} WHERE meterId = :meterId AND serviceLocCode = :locale"
    )
    fun findPayerServiceByMeterId(meterId: UUID, locale: String? = Locale.getDefault().language):
            Flow<List<MeterPayerServiceView>>

    @ExperimentalCoroutinesApi
    fun findDistinctPayerServiceByMeterId(meterId: UUID) =
        findPayerServiceByMeterId(meterId).distinctUntilChanged()

    // INSERTS:
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg textContent: ServiceTlEntity)

    @Transaction
    suspend fun insert(service: ServiceEntity, textContent: ServiceTlEntity) {
        service.servicePos = service.servicePos ?: nextPos()
        updatePos(service.servicePos!!)
        insert(service)
        insert(textContent)
    }

    // UPDATES:
    @Update
    suspend fun update(vararg textContent: ServiceTlEntity)

    @Transaction
    suspend fun update(service: ServiceEntity, textContent: ServiceTlEntity) {
        service.servicePos = service.servicePos ?: nextPos()
        updatePos(service.servicePos!!)
        update(service)
        update(textContent)
    }

    // DELETES:
    @Query("DELETE FROM ${ServiceEntity.TABLE_NAME} WHERE serviceId = :serviceId")
    suspend fun deleteById(serviceId: UUID)

    @Query("DELETE FROM ${ServiceEntity.TABLE_NAME}")
    suspend fun deleteAll()

    // API:
    @Query("SELECT IFNULL(MAX(servicePos), 0) FROM ${ServiceEntity.TABLE_NAME}")
    fun maxPos(): Int

    @Query("SELECT IFNULL(MAX(servicePos), 0) + 1 FROM ${ServiceEntity.TABLE_NAME}")
    fun nextPos(): Int

    @Query("UPDATE ${ServiceEntity.TABLE_NAME} SET servicePos = servicePos + 1 WHERE servicePos >= :pos")
    suspend fun updatePos(pos: Int)

    @Query("UPDATE ${PayerServiceCrossRefEntity.TABLE_NAME} SET isMeterOwner = 1 WHERE payerServiceId = :payerServiceId AND isMeterOwner = 0")
    suspend fun setPayerServiceMeterOwnerById(payerServiceId: UUID)

    @Query(
        """
UPDATE ${PayerServiceCrossRefEntity.TABLE_NAME} SET isMeterOwner = 0 
WHERE payerServiceId <> :payerServiceId 
    AND payersId = (SELECT ps.payersId FROM ${PayerServiceCrossRefEntity.TABLE_NAME} ps WHERE ps.payerServiceId = :payerServiceId)
    AND servicesId IN (SELECT serviceId FROM ${ServiceEntity.TABLE_NAME}
                        WHERE serviceMeterType = (
                            SELECT s.serviceMeterType FROM ${ServiceEntity.TABLE_NAME} s 
                                JOIN ${PayerServiceCrossRefEntity.TABLE_NAME} ps ON s.serviceId = ps.servicesId
                                    AND ps.payerServiceId = :payerServiceId)
                        )
    """
    )
    suspend fun clearPayerServiceMeterOwnerById(payerServiceId: UUID)

    @Transaction
    suspend fun payerServiceMeterOwnerById(payerServiceId: UUID) {
        clearPayerServiceMeterOwnerById(payerServiceId)
        setPayerServiceMeterOwnerById(payerServiceId)
    }
}
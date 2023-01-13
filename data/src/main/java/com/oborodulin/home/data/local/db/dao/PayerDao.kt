package com.oborodulin.home.data.local.db.dao

import androidx.room.*
import com.oborodulin.home.data.local.db.entities.PayerEntity
import com.oborodulin.home.data.local.db.entities.PayerServiceCrossRefEntity
import com.oborodulin.home.data.local.db.entities.PayerWithServices
import com.oborodulin.home.data.local.db.entities.ServiceEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import java.util.*

@Dao
interface PayerDao {
    // READS:
    @Query("SELECT * FROM payers")
    fun findAll(): Flow<List<PayerEntity>>

    @ExperimentalCoroutinesApi
    fun findAllDistinctUntilChanged() = findAll().distinctUntilChanged()

    @Query("SELECT * FROM payers WHERE payerId = :payerId")
    fun findById(payerId: UUID): Flow<PayerEntity>

    @ExperimentalCoroutinesApi
    fun findByIdDistinctUntilChanged(id: UUID) = findById(id).distinctUntilChanged()

    @Transaction
    @Query("SELECT * FROM payers")
    fun findPayersWithServices(): List<PayerWithServices>

    // INSERTS:
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg payer: PayerEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAll(payers: List<PayerEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg payerService: PayerServiceCrossRefEntity)

    suspend fun insert(payer: PayerEntity, service: ServiceEntity) =
        insert(
            PayerServiceCrossRefEntity(
                payersId = payer.payerId,
                servicesId = service.serviceId
            )
        )

    // UPDATES:
    @Update
    suspend fun update(vararg payer: PayerEntity)

    // DELETES:
    @Delete
    suspend fun delete(vararg payer: PayerEntity)

    @Delete
    suspend fun delete(payers: List<PayerEntity>)

    @Query("DELETE FROM payers")
    suspend fun deleteAll()
}
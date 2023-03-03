package com.oborodulin.home.data.local.db.dao

import androidx.room.*
import com.oborodulin.home.data.local.db.entities.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import java.util.*

@Dao
interface PayerDao : BaseDao<PayerEntity> {
    // READS:
    @Query("SELECT * FROM ${PayerEntity.TABLE_NAME} ORDER BY isFavorite DESC")
    fun findAll(): Flow<List<PayerEntity>>

    @ExperimentalCoroutinesApi
    fun findDistinctAll() = findAll().distinctUntilChanged()

    @Query("SELECT * FROM ${PayerEntity.TABLE_NAME} WHERE payerId = :payerId")
    fun findById(payerId: UUID): Flow<PayerEntity>

    @ExperimentalCoroutinesApi
    fun findDistinctById(id: UUID) = findById(id).distinctUntilChanged()

    @Query("SELECT * FROM ${PayerEntity.TABLE_NAME} WHERE isFavorite = 1")
    fun findFavorite(): Flow<PayerEntity>

    @ExperimentalCoroutinesApi
    fun findDistinctFavorite() = findFavorite().distinctUntilChanged()

    @Transaction
    @Query("SELECT * FROM ${PayerEntity.TABLE_NAME} ORDER BY isFavorite DESC")
    fun findPayersWithServices(): Flow<List<PayerWithServices>>

    // INSERTS:
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
    suspend fun update(vararg payerService: PayerServiceCrossRefEntity)

    // DELETES:
    @Query("DELETE FROM ${PayerEntity.TABLE_NAME} WHERE payerId = :payerId")
    suspend fun deleteById(payerId: UUID)

    @Delete
    suspend fun deleteService(vararg payerService: PayerServiceCrossRefEntity)

    @Query("DELETE FROM ${PayerServiceCrossRefEntity.TABLE_NAME} WHERE payerServiceId = :payerServiceId")
    suspend fun deleteServiceById(payerServiceId: UUID)

    @Query("DELETE FROM ${PayerEntity.TABLE_NAME}")
    suspend fun deleteAll()

    // API:
    @Query("UPDATE ${PayerEntity.TABLE_NAME} SET isFavorite = 1 WHERE payerId = :payerId AND isFavorite = 0")
    suspend fun setFavoriteById(payerId: UUID)

    @Query("UPDATE ${PayerEntity.TABLE_NAME} SET isFavorite = 0 WHERE payerId <> :payerId AND isFavorite = 1")
    suspend fun clearFavoritesById(payerId: UUID)

    @Transaction
    suspend fun favoriteById(payerId: UUID) {
        clearFavoritesById(payerId)
        setFavoriteById(payerId)
    }
}
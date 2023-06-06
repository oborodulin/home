package com.oborodulin.home.data.local.db.dao

import androidx.room.*
import com.oborodulin.home.data.local.db.entities.*
import com.oborodulin.home.data.util.Constants.DB_FALSE
import com.oborodulin.home.data.util.Constants.DB_TRUE
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import java.util.*

@Dao
interface PayerDao { //: BaseDao<PayerEntity>
    // READS:
    @Query("SELECT * FROM ${PayerEntity.TABLE_NAME} ORDER BY isFavorite DESC")
    fun findAll(): Flow<List<PayerEntity>>

    @ExperimentalCoroutinesApi
    fun findDistinctAll() = findAll().distinctUntilChanged()

    @Query("SELECT * FROM ${PayerEntity.TABLE_NAME} WHERE payerId = :payerId")
    fun findById(payerId: UUID): Flow<PayerEntity>

    @ExperimentalCoroutinesApi
    fun findDistinctById(id: UUID) = findById(id).distinctUntilChanged()

    @Query("SELECT * FROM ${PayerEntity.TABLE_NAME} WHERE ercCode = :ercCode LIMIT 1")
    fun findByErcCode(ercCode: String): Flow<PayerEntity>

    @Query("SELECT EXISTS (SELECT * FROM ${PayerEntity.TABLE_NAME} WHERE ercCode = :ercCode LIMIT 1)")
    fun existsByErcCode(ercCode: String): Boolean

    @Query("SELECT * FROM ${PayerEntity.TABLE_NAME} WHERE isFavorite = $DB_TRUE")
    fun findFavorite(): Flow<PayerEntity>

    @ExperimentalCoroutinesApi
    fun findDistinctFavorite() = findFavorite().distinctUntilChanged()

    @Transaction
    @Query("SELECT * FROM ${PayerEntity.TABLE_NAME} ORDER BY isFavorite DESC")
    fun findPayersWithServices(): Flow<List<PayerWithServices>>

    // INSERTS:
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(payer: PayerEntity)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(vararg payers: PayerEntity)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(payers: List<PayerEntity>)

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
    suspend fun update(payer: PayerEntity)

    @Update
    suspend fun update(vararg payers: PayerEntity)

    @Update
    suspend fun update(vararg payerService: PayerServiceCrossRefEntity)

    // DELETES:
    @Delete
    suspend fun delete(payer: PayerEntity)

    @Delete
    suspend fun delete(vararg payers: PayerEntity)

    @Delete
    suspend fun delete(payers: List<PayerEntity>)

    @Query("DELETE FROM ${PayerEntity.TABLE_NAME} WHERE payerId = :payerId")
    suspend fun deleteById(payerId: UUID)

    @Delete
    suspend fun deleteService(vararg payerService: PayerServiceCrossRefEntity)

    @Query("DELETE FROM ${PayerServiceCrossRefEntity.TABLE_NAME} WHERE payerServiceId = :payerServiceId")
    suspend fun deleteServiceById(payerServiceId: UUID)

    @Query("DELETE FROM ${PayerServiceCrossRefEntity.TABLE_NAME} WHERE payersId = :payerId")
    suspend fun deleteServicesByPayerId(payerId: UUID)

    @Query("DELETE FROM ${PayerEntity.TABLE_NAME}")
    suspend fun deleteAll()

    // API:
    @Query("UPDATE ${PayerEntity.TABLE_NAME} SET isFavorite = $DB_TRUE WHERE payerId = :payerId AND isFavorite = $DB_FALSE")
    suspend fun setFavoriteById(payerId: UUID)

    @Query("UPDATE ${PayerEntity.TABLE_NAME} SET isFavorite = $DB_FALSE WHERE payerId <> :payerId AND isFavorite = $DB_TRUE")
    suspend fun clearFavoritesById(payerId: UUID)

    @Transaction
    suspend fun makeFavoriteById(payerId: UUID) {
        clearFavoritesById(payerId)
        setFavoriteById(payerId)
    }
}
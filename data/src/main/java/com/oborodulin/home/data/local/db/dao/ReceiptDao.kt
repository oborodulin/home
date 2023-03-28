package com.oborodulin.home.data.local.db.dao

import androidx.room.*
import com.oborodulin.home.common.util.Constants
import com.oborodulin.home.data.local.db.entities.*
import com.oborodulin.home.data.util.Constants.DB_FALSE
import com.oborodulin.home.data.util.Constants.DB_TRUE
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import java.util.*

@Dao
interface ReceiptDao : BaseDao<ReceiptEntity> {
    // READS:
    @Query("SELECT * FROM ${ReceiptEntity.TABLE_NAME} ORDER BY receiptYear, receiptMonth")
    fun findAll(): Flow<List<ReceiptEntity>>

    @ExperimentalCoroutinesApi
    fun findDistinctAll() = findAll().distinctUntilChanged()

    @Query("SELECT * FROM ${ReceiptEntity.TABLE_NAME} WHERE receiptId = :id")
    fun findById(id: UUID): Flow<ReceiptEntity>

    @ExperimentalCoroutinesApi
    fun findDistinctById(id: UUID) = findById(id).distinctUntilChanged()

    @Query("SELECT * FROM ${ReceiptEntity.TABLE_NAME} WHERE payersId = :payerId")
    fun findByPayerId(payerId: UUID): Flow<ReceiptEntity>

    @ExperimentalCoroutinesApi
    fun findDistinctByPayerId(payerId: UUID) = findByPayerId(payerId).distinctUntilChanged()

    @Transaction
    @Query("SELECT * FROM ${ReceiptEntity.TABLE_NAME} ORDER BY receiptYear, receiptMonth")
    fun findReceiptWithLines(): Flow<List<ReceiptWithLines>>

    // INSERTS:
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg lines: ReceiptLineEntity)

    // UPDATES:
    @Update
    suspend fun update(vararg lines: ReceiptLineEntity)

    // DELETES:
    @Query("DELETE FROM ${ReceiptEntity.TABLE_NAME} WHERE receiptId = :receiptId")
    suspend fun deleteById(receiptId: UUID)

    @Query("DELETE FROM ${ReceiptEntity.TABLE_NAME}")
    suspend fun deleteAll()

    @Delete
    suspend fun deleteLine(vararg lines: ReceiptLineEntity)

    @Query("DELETE FROM ${ReceiptLineEntity.TABLE_NAME} WHERE receiptLineId = :receiptLineId")
    suspend fun deleteLineById(receiptLineId: UUID)

    @Query("DELETE FROM ${ReceiptLineEntity.TABLE_NAME} WHERE receiptsId = :receiptId")
    suspend fun deleteLinesByReceiptId(receiptId: UUID)
}
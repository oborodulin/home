package com.oborodulin.home.data.local.db.repositories.sources.local

import com.oborodulin.home.data.local.db.entities.PayerEntity
import kotlinx.coroutines.flow.Flow
import java.util.*

interface LocalPayerDataSource {
    fun getPayers(): Flow<List<PayerEntity>>
    fun getPayer(payerId: UUID): Flow<PayerEntity>
    fun getFavoritePayer(): Flow<PayerEntity>
    suspend fun insertPayer(payer: PayerEntity)
    suspend fun updatePayer(payer: PayerEntity)
    suspend fun deletePayer(payer: PayerEntity)
    suspend fun deletePayerById(payerId: UUID)
    suspend fun makeFavoritePayerById(payerId: UUID)
    suspend fun deletePayers(payers: List<PayerEntity>)
    suspend fun deleteAllPayers()
}
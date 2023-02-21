package com.oborodulin.home.data.local.db.repositories

import com.oborodulin.home.domain.model.Payer
import kotlinx.coroutines.flow.Flow
import java.util.*

interface PayerDataSource {
    fun getPayers(): Flow<List<Payer>>
    fun getPayer(payerId: UUID): Flow<Payer>
    fun getFavoritePayer(): Flow<Payer>
    suspend fun savePayer(payer: Payer)
    suspend fun deletePayer(payer: Payer)
    suspend fun deletePayerById(payerId: UUID)
    suspend fun favoritePayerById(payerId: UUID)
    suspend fun deletePayers(payers: List<Payer>)
    suspend fun deletePayers()
}
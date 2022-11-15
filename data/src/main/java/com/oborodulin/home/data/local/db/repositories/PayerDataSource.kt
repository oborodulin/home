package com.oborodulin.home.data.local.db.repositories

import com.oborodulin.home.domain.model.Payer
import kotlinx.coroutines.flow.Flow
import java.util.*

interface PayerDataSource {
    fun getPayers(): Flow<List<Payer>>
    fun getPayer(id: UUID): Flow<Payer>
    suspend fun addPayer(payer: Payer)
    suspend fun updatePayer(payer: Payer)
    suspend fun savePayer(payer: Payer)
    suspend fun deletePayer(payer: Payer)
    suspend fun deletePayers(payers: List<Payer>)
    suspend fun deletePayers()
}
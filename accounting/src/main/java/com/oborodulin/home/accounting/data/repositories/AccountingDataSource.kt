package com.oborodulin.home.accounting.data.repositories

import com.oborodulin.home.accounting.domain.model.Payer
import kotlinx.coroutines.flow.Flow
import java.util.*

interface AccountingDataSource {
    suspend fun getPayers(): Flow<List<Payer>>
    suspend fun getPayer(id: UUID): Payer?
    suspend fun addPayer(payer: Payer)
    suspend fun updatePayer(payer: Payer)
    suspend fun deletePayer(payer: Payer)
    suspend fun deletePayers(payers: List<Payer>)
    suspend fun deletePayers()
}
package com.oborodulin.home.accounting.data.repositories

import com.oborodulin.home.accounting.domain.model.Payer
import com.oborodulin.home.data.local.db.entities.PayerEntity
import kotlinx.coroutines.flow.Flow
import java.util.*

interface AccountingDataSource {
    fun getPayers(): Flow<List<PayerEntity>>
    fun getPayer(id: UUID): Flow<PayerEntity>
    suspend fun addPayer(payer: Payer)
    suspend fun updatePayer(payer: Payer)
    suspend fun savePayer(payer: Payer)
    suspend fun deletePayer(payer: Payer)
    suspend fun deletePayers(payers: List<Payer>)
    suspend fun deletePayers()
}
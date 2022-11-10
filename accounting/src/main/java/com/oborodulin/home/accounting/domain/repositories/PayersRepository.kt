package com.oborodulin.home.accounting.domain.repositories

import com.oborodulin.home.accounting.domain.model.Payer
import com.oborodulin.home.data.local.db.entities.PayerEntity
import kotlinx.coroutines.flow.Flow
import java.util.*

interface PayersRepository {
    fun getAll(): Flow<List<PayerEntity>>

    fun get(id: UUID): Flow<PayerEntity>

    suspend fun add(payer: Payer)

    suspend fun update(payer: Payer)

    suspend fun save(payer: Payer)

    suspend fun delete(payer: Payer)

    suspend fun deleteAll()
}
package com.oborodulin.home.accounting.domain.repositories

import com.oborodulin.home.accounting.domain.model.Payer
import com.oborodulin.home.data.local.db.entities.PayerEntity
import kotlinx.coroutines.flow.Flow
import java.util.*

interface PayersRepository {
    fun getAll(): Flow<List<PayerEntity>>

    fun get(id: UUID): Flow<PayerEntity>

    fun add(payer: Payer): Flow<Payer>

    fun update(payer: Payer): Flow<Payer>

    fun save(payer: Payer): Flow<Payer>

    fun delete(payer: Payer): Flow<Payer>

    suspend fun deleteAll()
}
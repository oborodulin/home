package com.oborodulin.home.accounting.domain.repositories

import com.oborodulin.home.accounting.domain.model.Payer
import kotlinx.coroutines.flow.Flow
import java.util.*

interface PayersRepository {
    fun getAll(): Flow<List<Payer>>

    fun get(id: UUID): Flow<Payer>

    suspend fun add(payer: Payer)

    suspend fun update(payer: Payer)

    suspend fun delete(payer: Payer)

    suspend fun deleteAll()
}
package com.oborodulin.home.domain.repositories

import com.oborodulin.home.domain.model.Payer
import kotlinx.coroutines.flow.Flow
import java.util.*

interface PayersRepository {
    fun getAll(): Flow<List<Payer>>

    fun get(id: UUID): Flow<Payer>

    fun add(payer: Payer): Flow<Payer>

    fun update(payer: Payer): Flow<Payer>

    fun save(payer: Payer): Flow<Payer>

    fun delete(payer: Payer): Flow<Payer>

    suspend fun deleteAll()
}
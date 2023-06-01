package com.oborodulin.home.domain.repositories

import com.oborodulin.home.domain.model.Payer
import kotlinx.coroutines.flow.Flow
import java.util.*

interface PayersRepository {
    fun getAll(): Flow<List<Payer>>
    fun get(payerId: UUID): Flow<Payer>
    fun getFavorite(): Flow<Payer>
    fun save(payer: Payer): Flow<Payer>
    fun delete(payer: Payer): Flow<Payer>
    fun deleteById(payerId: UUID): Flow<UUID>
    fun makeFavoriteById(payerId: UUID): Flow<UUID>
    suspend fun deleteAll()
}
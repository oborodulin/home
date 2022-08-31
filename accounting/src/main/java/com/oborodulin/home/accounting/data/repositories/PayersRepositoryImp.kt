package com.oborodulin.home.accounting.data.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.oborodulin.home.accounting.domain.model.Payer
import com.oborodulin.home.accounting.domain.repositories.PayersRepository
import java.util.*

import javax.inject.Inject

class PayersRepositoryImp @Inject constructor(
    private val accountingDataSource: AccountingDataSource
) : PayersRepository {
    override suspend fun getAll() = accountingDataSource.getPayers()

    override suspend fun get(id: UUID) = accountingDataSource.getPayer(id)

    override suspend fun add(payer: Payer) = accountingDataSource.addPayer(payer)

    override suspend fun update(payer: Payer) = accountingDataSource.updatePayer(payer)

    override suspend fun delete(payer: Payer) = accountingDataSource.deletePayer(payer)

    override suspend fun deleteAll() = accountingDataSource.deletePayers()

    /*
        fun nowPlaying(): Flow<PagingData<NetworkMovie>> {
            val config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = true,
                prefetchDistance = 5
            )
            return Pager(config) {
                AccountingDataSource(
                    nowPlayingUseCase = accountingUseCase
                )
            }.flow
        }
      */
}
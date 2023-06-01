package com.oborodulin.home.data.local.db.repositories

import com.oborodulin.home.data.local.db.mappers.PayerMappers
import com.oborodulin.home.data.local.db.repositories.sources.local.LocalPayerDataSource
import com.oborodulin.home.domain.model.Payer
import com.oborodulin.home.domain.repositories.PayersRepository
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class PayersRepositoryImpl @Inject constructor(
    private val localPayerDataSource: LocalPayerDataSource,
    private val mappers: PayerMappers
) : PayersRepository {
    override fun getAll() = localPayerDataSource.getPayers()
        .map(mappers.payerEntityListToPayerListMapper::map)

    override fun get(payerId: UUID) =
        localPayerDataSource.getPayer(payerId).map(mappers.payerEntityToPayerMapper::map)

    override fun getFavorite() =
        localPayerDataSource.getFavoritePayer().map(mappers.payerEntityToPayerMapper::map)

    override fun save(payer: Payer) = flow {
        if (payer.id == null) {
            localPayerDataSource.insertPayer(mappers.payerToPayerEntityMapper.map(payer))
        } else {
            localPayerDataSource.updatePayer(mappers.payerToPayerEntityMapper.map(payer))
        }
        emit(payer)
    }

    override fun delete(payer: Payer) = flow {
        localPayerDataSource.deletePayer(mappers.payerToPayerEntityMapper.map(payer))
        this.emit(payer)
    }

    override fun deleteById(payerId: UUID) = flow {
        localPayerDataSource.deletePayerById(payerId)
        this.emit(payerId)
    }

    override suspend fun deleteAll() = localPayerDataSource.deleteAllPayers()

    override fun makeFavoriteById(payerId: UUID) = flow {
        localPayerDataSource.makeFavoritePayerById(payerId)
        this.emit(payerId)
    }
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
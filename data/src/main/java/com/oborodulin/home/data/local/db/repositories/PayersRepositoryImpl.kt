package com.oborodulin.home.data.local.db.repositories

import com.oborodulin.home.data.local.db.mappers.PayerEntityListToPayerListMapper
import com.oborodulin.home.data.local.db.mappers.PayerEntityToPayerMapper
import com.oborodulin.home.data.local.db.mappers.PayerToPayerEntityMapper
import com.oborodulin.home.domain.model.Payer
import com.oborodulin.home.domain.repositories.PayersRepository
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject

class PayersRepositoryImpl @Inject constructor(
    private val payerDataSource: PayerDataSource,
    private val payerEntityListToPayerListMapper: PayerEntityListToPayerListMapper,
    private val payerEntityToPayerMapper: PayerEntityToPayerMapper,
    private val payerToPayerEntityMapper: PayerToPayerEntityMapper
) : PayersRepository {
    override fun getAll() = payerDataSource.getPayers()
        .map(payerEntityListToPayerListMapper::map)

    override fun get(payerId: UUID) =
        payerDataSource.getPayer(payerId).map(payerEntityToPayerMapper::map)

    override fun getFavorite() =
        payerDataSource.getFavoritePayer().map(payerEntityToPayerMapper::map)

    override fun save(payer: Payer) = flow {
        if (payer.id == null) {
            payerDataSource.insertPayer(payerToPayerEntityMapper.map(payer))
        } else {
            payerDataSource.updatePayer(payerToPayerEntityMapper.map(payer))
        }
        emit(payer)
    }

    override fun delete(payer: Payer) = flow {
        payerDataSource.deletePayer(payerToPayerEntityMapper.map(payer))
        this.emit(payer)
    }

    override fun deleteById(payerId: UUID) = flow {
        payerDataSource.deletePayerById(payerId)
        this.emit(payerId)
    }

    override suspend fun deleteAll() = payerDataSource.deletePayers()

    override fun favoriteById(payerId: UUID) = flow {
        payerDataSource.favoritePayerById(payerId)
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
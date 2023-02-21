package com.oborodulin.home.data.local.db.repositories

import com.oborodulin.home.domain.model.Payer
import com.oborodulin.home.domain.repositories.PayersRepository
import kotlinx.coroutines.flow.flow
import java.util.*
import javax.inject.Inject

class PayersRepositoryImp @Inject constructor(
    private val payerDataSource: PayerDataSource
) : PayersRepository {
    override fun getAll() = payerDataSource.getPayers()

    override fun get(payerId: UUID) = payerDataSource.getPayer(payerId)

    override fun getFavorite() = payerDataSource.getFavoritePayer()

    override fun save(payer: Payer) = flow {
        payerDataSource.savePayer(payer)
        emit(payer)
    }

    override fun delete(payer: Payer) = flow {
        payerDataSource.deletePayer(payer)
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
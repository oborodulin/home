package com.oborodulin.home.accounting.data.repositories

import com.oborodulin.home.accounting.domain.model.Payer
import com.oborodulin.home.accounting.domain.repositories.PayersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.*
import javax.inject.Inject

class PayersRepositoryImp @Inject constructor(
    private val accountingDataSource: AccountingDataSource
) : PayersRepository {
    override fun getAll() = accountingDataSource.getPayers()

    override fun get(id: UUID) = accountingDataSource.getPayer(id)

    override fun add(payer: Payer): Flow<Payer> = flow {
        accountingDataSource.addPayer(payer)
        emit(payer)
    }

    override fun update(payer: Payer): Flow<Payer> = flow {
        accountingDataSource.updatePayer(payer)
        emit(payer)
    }

    override fun save(payer: Payer): Flow<Payer> = flow {
        accountingDataSource.savePayer(payer)
        emit(payer)
    }

    override fun delete(payer: Payer): Flow<Payer> = flow {
        accountingDataSource.deletePayer(payer)
        this.emit(payer)
    }

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
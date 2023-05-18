package com.oborodulin.home.data.local.db.repositories

import com.oborodulin.home.common.di.IoDispatcher
import com.oborodulin.home.data.local.db.dao.PayerDao
import com.oborodulin.home.data.local.db.entities.PayerEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

/**
 * Created by o.borodulin on 08.August.2022
 */
@OptIn(ExperimentalCoroutinesApi::class)
class PayerDataSourceImp @Inject constructor(
    private val payerDao: PayerDao,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : PayerDataSource {
    override fun getPayers() = payerDao.findDistinctAll()

    override fun getPayer(payerId: UUID) = payerDao.findDistinctById(payerId)

    override fun getFavoritePayer() = payerDao.findDistinctFavorite()

    override suspend fun insertPayer(payer: PayerEntity) = withContext(dispatcher) {
        payerDao.insert(payer)
    }

    override suspend fun updatePayer(payer: PayerEntity) = withContext(dispatcher) {
        payerDao.update(payer)
    }

    override suspend fun deletePayer(payer: PayerEntity) = withContext(dispatcher) {
        payerDao.delete(payer)
    }

    override suspend fun deletePayerById(payerId: UUID) = withContext(dispatcher) {
        payerDao.deleteById(payerId)
    }

    override suspend fun deletePayers(payers: List<PayerEntity>) = withContext(dispatcher) {
        payerDao.delete(payers)
    }

    override suspend fun deletePayers() = withContext(dispatcher) {
        payerDao.deleteAll()
    }

    override suspend fun favoritePayerById(payerId: UUID) = withContext(dispatcher) {
        payerDao.favoriteById(payerId)
    }
}

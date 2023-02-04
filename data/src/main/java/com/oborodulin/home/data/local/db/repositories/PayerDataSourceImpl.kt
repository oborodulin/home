package com.oborodulin.home.data.local.db.repositories

import com.oborodulin.home.common.di.IoDispatcher
import com.oborodulin.home.data.local.db.dao.PayerDao
import com.oborodulin.home.data.local.db.mappers.PayerEntityListToPayerListMapper
import com.oborodulin.home.data.local.db.mappers.PayerEntityToPayerMapper
import com.oborodulin.home.data.local.db.mappers.PayerToPayerEntityMapper
import com.oborodulin.home.domain.model.Payer
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

/**
 * Created by o.borodulin on 08.August.2022
 */
@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class PayerDataSourceImpl @Inject constructor(
    private val payerDao: PayerDao,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val payerEntityListToPayerListMapper: PayerEntityListToPayerListMapper,
    private val payerEntityToPayerMapper: PayerEntityToPayerMapper,
    private val payerToPayerEntityMapper: PayerToPayerEntityMapper
) : PayerDataSource {
    override fun getPayers() = payerDao.findAllDistinctUntilChanged()
        .map(payerEntityListToPayerListMapper::map)

    override fun getPayer(payerId: UUID) =
        payerDao.findByIdDistinctUntilChanged(payerId).map(payerEntityToPayerMapper::map)

    override suspend fun savePayer(payer: Payer) = withContext(dispatcher) {
        if (payer.id == null) {
            payerDao.insert(payerToPayerEntityMapper.map(payer))
        } else {
            payerDao.update(payerToPayerEntityMapper.map(payer))
        }
    }

    override suspend fun deletePayer(payer: Payer) = withContext(dispatcher) {
        payerDao.delete(payerToPayerEntityMapper.map(payer))
    }

    override suspend fun deletePayerById(payerId: UUID) = withContext(dispatcher) {
        payerDao.deleteById(payerId)
    }

    override suspend fun deletePayers(payers: List<Payer>) = withContext(dispatcher) {
        payerDao.delete(payers.map { payerToPayerEntityMapper.map(it) })
    }

    override suspend fun deletePayers() = withContext(dispatcher) {
        payerDao.deleteAll()
    }

    override suspend fun favoritePayerById(payerId: UUID) = withContext(dispatcher) {
        payerDao.favoriteById(payerId)
    }
}

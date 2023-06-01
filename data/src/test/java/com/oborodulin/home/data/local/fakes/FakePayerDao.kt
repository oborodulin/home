package com.oborodulin.home.data.local.fakes

import androidx.room.*
import com.oborodulin.home.data.local.db.dao.PayerDao
import com.oborodulin.home.data.local.db.entities.*
import com.oborodulin.home.data.util.Constants.DB_FALSE
import com.oborodulin.home.data.util.Constants.DB_TRUE
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import java.util.*

class FakePayerDao : FakeBaseDao<PayerEntity>(), PayerDao {
    private var payerServices = HashMap<UUID, PayerServiceCrossRefEntity>()

    // READS:
    override fun findAll() = flow { emit(objs.values.toList()) }

    @ExperimentalCoroutinesApi
    override fun findDistinctAll() = findAll().distinctUntilChanged()

    override fun findById(payerId: UUID) =
        flow { emit(objs.values.toList().first { it.payerId == payerId }) }

    @ExperimentalCoroutinesApi
    override fun findDistinctById(id: UUID) = findById(id).distinctUntilChanged()

    override fun findByErcCode(ercCode: String) =
        flow { emit(objs.values.toList().first { it.ercCode == ercCode }) }

    override fun existsByErcCode(ercCode: String) =
        objs.values.toList().firstOrNull { it.ercCode == ercCode } != null

    override fun findFavorite() = flow { emit(objs.values.toList().first { it.isFavorite }) }

    @ExperimentalCoroutinesApi
    override fun findDistinctFavorite() = findFavorite().distinctUntilChanged()

    override fun findPayersWithServices() = flow {
        emit(
            objs.values.toList().map { PayerWithServices(payer = it, services = emptyList()) })
    }

    // INSERTS:
    override suspend fun insert(vararg payerService: PayerServiceCrossRefEntity) {
        delay(1000)
        payerService.forEach { this.payerServices[it.id()] = it }
    }

    override suspend fun insert(payer: PayerEntity, service: ServiceEntity) {
        val payerService = PayerServiceCrossRefEntity(
            payersId = payer.payerId,
            servicesId = service.serviceId
        )
        delay(1000)
        this.payerServices[payerService.id()] = payerService
    }

    // UPDATES:
    override suspend fun update(vararg payerService: PayerServiceCrossRefEntity) {
        delay(1000)
        payerService.forEach { this.payerServices[it.id()] = it }
    }

    // DELETES:
    override suspend fun deleteById(payerId: UUID) {
        delay(1000)
        objs.remove(payerId)
    }

    override suspend fun deleteService(vararg payerService: PayerServiceCrossRefEntity) {
        delay(1000)
        payerService.forEach { this.payerServices.remove(it.id()) }
    }

    override suspend fun deleteServiceById(payerServiceId: UUID) {
        delay(1000)
        this.payerServices.remove(payerServiceId)
    }

    override suspend fun deleteAll() {
        delay(1000)
        objs.clear()
    }

    // API:
    override suspend fun setFavoriteById(payerId: UUID) {
        delay(1000)
        val payer = objs.values.firstOrNull { it.payerId == payerId && !it.isFavorite }
        if (payer != null) objs[payerId] = payer.copy(isFavorite = true)
    }

    override suspend fun clearFavoritesById(payerId: UUID) {
        delay(1000)
        objs.values.filter { it.payerId != payerId && it.isFavorite }
            .forEach { it.copy(isFavorite = false) }
    }

    override suspend fun makeFavoriteById(payerId: UUID) {
        clearFavoritesById(payerId)
        setFavoriteById(payerId)
    }
}
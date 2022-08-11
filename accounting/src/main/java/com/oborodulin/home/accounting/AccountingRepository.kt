package com.oborodulin.home.accounting

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.lifecycle.LiveData
import com.oborodulin.home.domain.database.HomeDatabase
import com.oborodulin.home.domain.entity.Payer
import java.util.*
import java.util.concurrent.Executors

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.oborodulin.home.domain.usecase.AccountingUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AccountingRepository @Inject constructor(
    context: Context//,
    //private val accountingUseCase: AccountingUseCase
) {
    private val payerDao = HomeDatabase.newInstance(context.applicationContext).payerDao()
    private val executor = Executors.newSingleThreadExecutor()

    //    fun getAll(): LiveData<List<Payer>> = payerDao.getAll()
//    fun get(id: UUID): LiveData<Payer?> = payerDao.get(id)
    suspend fun getAll() = withContext(Dispatchers.IO) {
        payerDao.getAll()
    }

    suspend fun get(id: UUID) = withContext(Dispatchers.IO) {
        payerDao.get(id)
    }

    suspend fun add(payer: Payer) = withContext(Dispatchers.IO) {
        payerDao.add(payer)
    }

    suspend fun update(payer: Payer) = withContext(Dispatchers.IO) {
        payerDao.update(payer)
    }

    suspend fun delete(payer: Payer) = withContext(Dispatchers.IO) {
        payerDao.delete(payer)
    }

    suspend fun deleteAll() = withContext(Dispatchers.IO) {
        payerDao.deleteAll()
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
    companion object {
        private var INSTANCE: AccountingRepository? = null
        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = AccountingRepository(context)
            }
        }

        fun getInstance(): AccountingRepository {
            return INSTANCE ?: throw IllegalStateException("PayerRepository must be initialized")
        }
    }
}
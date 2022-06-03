package com.oborodulin.home.domain.payer

import android.content.Context
import androidx.lifecycle.LiveData
import com.oborodulin.home.database.HomeDatabase
import java.util.*
import java.util.concurrent.Executors

class PayerRepository private constructor(context: Context) {
    private val database: HomeDatabase = HomeDatabase.newInstance(context.applicationContext)
    private val payerDao = database.payerDao()
    private val executor = Executors.newSingleThreadExecutor()

    fun getAll(): LiveData<List<Payer>> = payerDao.getAll()
    fun get(id: UUID): LiveData<Payer?> = payerDao.get(id)

    fun add(payer: Payer) {
        executor.execute {
            payerDao.add(payer)
        }
    }

    fun update(payer: Payer) {
        executor.execute {
            payerDao.update(payer)
        }
    }

    fun delete(payer: Payer) {
        executor.execute {
            payerDao.delete(payer)
        }
    }

    fun deleteAll() {
        executor.execute {
            payerDao.deleteAll()
        }
    }

    companion object {
        private var INSTANCE: PayerRepository? = null
        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = PayerRepository(context)
            }
        }

        fun getInstance(): PayerRepository {
            return INSTANCE ?: throw IllegalStateException("PayerRepository must be initialized")
        }
    }
}
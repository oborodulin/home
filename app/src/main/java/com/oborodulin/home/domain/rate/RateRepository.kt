package com.oborodulin.home.domain.rate

import android.content.Context
import androidx.lifecycle.LiveData
//import com.oborodulin.home.domain.database.HomeDatabase
//import com.oborodulin.home.domain.entity.Rate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import java.util.*

class RateRepository private constructor(context: Context) {
    private var appScope: CoroutineScope?
        get() = appScope
        set(value) {
            appScope = value
        }
/*    private val database: HomeDatabase = HomeDatabase.newInstance(context.applicationContext)
    private val serviceDao = database.serviceDao()
    private val rateDao = database.rateDao()

    fun getAll(): LiveData<List<Rate>> = rateDao.getAll()
    fun get(id: UUID): LiveData<Rate?> = rateDao.get(id)

    suspend fun add(rate: Rate) {
        withContext(appScope!!.coroutineContext) {
            rateDao.add(rate)
        }
    }

    suspend fun update(rate: Rate) {
        withContext(appScope!!.coroutineContext) {
            rateDao.update(rate)
        }
    }

    suspend fun delete(rate: Rate) {
        withContext(appScope!!.coroutineContext) {
            rateDao.delete(rate)
        }
    }

    suspend fun deleteAll() {
        withContext(appScope!!.coroutineContext) {
            rateDao.deleteAll()
        }
    }

    companion object {
        private var INSTANCE: RateRepository? = null
        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = RateRepository(context)
            }
        }

        fun getInstance(appScope: CoroutineScope): RateRepository {
            INSTANCE?.appScope = appScope
            return INSTANCE ?: throw IllegalStateException("RateRepository must be initialized")
        }
    }
 */
}
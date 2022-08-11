package com.oborodulin.home.domain.service

import android.content.Context
import androidx.lifecycle.LiveData
import com.oborodulin.home.domain.database.HomeDatabase
import com.oborodulin.home.domain.entity.Service
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import java.util.*

class ServiceRepository private constructor(context: Context) {
    private var appScope: CoroutineScope?
        get() = appScope
        set(value) {
            appScope = value
        }
 /*   private val database: HomeDatabase = HomeDatabase.newInstance(context.applicationContext)
    private val serviceDao = database.serviceDao()

    fun getAll(): LiveData<List<Service>> = serviceDao.getAll()
    fun get(id: UUID): LiveData<Service?> = serviceDao.get(id)

    suspend fun add(service: Service) {
        withContext(appScope!!.coroutineContext) {
            serviceDao.add(service)
        }
    }

    suspend fun update(service: Service) {
        withContext(appScope!!.coroutineContext) {
            serviceDao.update(service)
        }
    }

    suspend fun delete(service: Service) {
        withContext(appScope!!.coroutineContext) {
            serviceDao.delete(service)
        }
    }

    suspend fun deleteAll() {
        withContext(appScope!!.coroutineContext) {
            serviceDao.deleteAll()
        }
    }

    fun maxDisplayPos(): LiveData<Int> = serviceDao.maxDisplayPos()
    fun nextDisplayPos(): LiveData<Int> = serviceDao.nextDisplayPos()

    companion object {
        private var INSTANCE: ServiceRepository? = null
        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = ServiceRepository(context)
            }
        }

        fun getInstance(appScope: CoroutineScope): ServiceRepository {
            INSTANCE?.appScope = appScope
            return INSTANCE ?: throw IllegalStateException("ServiceRepository must be initialized")
        }
    }

  */
}
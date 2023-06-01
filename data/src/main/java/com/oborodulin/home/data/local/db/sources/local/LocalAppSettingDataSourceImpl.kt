package com.oborodulin.home.data.local.db.sources.local

import com.oborodulin.home.common.di.IoDispatcher
import com.oborodulin.home.data.local.db.dao.AppSettingDao
import com.oborodulin.home.data.local.db.entities.AppSettingEntity
import com.oborodulin.home.data.local.db.repositories.sources.local.LocalAppSettingDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

/**
 * Created by o.borodulin on 08.August.2022
 */
@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class LocalAppSettingDataSourceImpl @Inject constructor(
    private val appSettingDao: AppSettingDao,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : LocalAppSettingDataSource {
    override fun getAppSettings() = appSettingDao.findDistinctAll()

    override fun getAppSetting(settingId: UUID) = appSettingDao.findDistinctById(settingId)

    override suspend fun insertAppSetting(setting: AppSettingEntity) = withContext(dispatcher) {
        appSettingDao.insert(setting)
    }

    override suspend fun updateAppSetting(setting: AppSettingEntity) = withContext(dispatcher) {
        appSettingDao.update(setting)
    }

    override suspend fun deleteAppSetting(setting: AppSettingEntity) = withContext(dispatcher) {
        appSettingDao.delete(setting)
    }

    override suspend fun deleteAppSettingById(settingId: UUID) = withContext(dispatcher) {
        appSettingDao.deleteById(settingId)
    }

    override suspend fun deleteAppSettings(settings: List<AppSettingEntity>) =
        withContext(dispatcher) {
            appSettingDao.delete(settings)
        }

    override suspend fun deleteAppSettings() = withContext(dispatcher) {
        appSettingDao.deleteAll()
    }
}

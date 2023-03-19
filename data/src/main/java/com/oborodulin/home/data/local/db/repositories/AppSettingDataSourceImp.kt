package com.oborodulin.home.data.local.db.repositories

import com.oborodulin.home.common.di.IoDispatcher
import com.oborodulin.home.data.local.db.dao.AppSettingDao
import com.oborodulin.home.data.local.db.mappers.AppSettingEntityListToAppSettingListMapper
import com.oborodulin.home.data.local.db.mappers.AppSettingEntityToAppSettingMapper
import com.oborodulin.home.data.local.db.mappers.AppSettingToAppSettingEntityMapper
import com.oborodulin.home.domain.model.AppSetting
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

/**
 * Created by o.borodulin on 08.August.2022
 */
@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class AppSettingDataSourceImp @Inject constructor(
    private val appSettingDao: AppSettingDao,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val appSettingEntityListToAppSettingListMapper: AppSettingEntityListToAppSettingListMapper,
    private val appSettingEntityToAppSettingMapper: AppSettingEntityToAppSettingMapper,
    private val appSettingToAppSettingEntityMapper: AppSettingToAppSettingEntityMapper
) : AppSettingDataSource {
    override fun getAppSettings() = appSettingDao.findDistinctAll()
        .map(appSettingEntityListToAppSettingListMapper::map)

    override fun getAppSetting(settingId: UUID) =
        appSettingDao.findDistinctById(settingId).map(appSettingEntityToAppSettingMapper::map)

    override suspend fun saveAppSetting(setting: AppSetting) = withContext(dispatcher) {
        if (setting.id == null) {
            appSettingDao.insert(appSettingToAppSettingEntityMapper.map(setting))
        } else {
            appSettingDao.update(appSettingToAppSettingEntityMapper.map(setting))
        }
    }

    override suspend fun deleteAppSetting(setting: AppSetting) = withContext(dispatcher) {
        appSettingDao.delete(appSettingToAppSettingEntityMapper.map(setting))
    }

    override suspend fun deleteAppSettingById(settingId: UUID) = withContext(dispatcher) {
        appSettingDao.deleteById(settingId)
    }

    override suspend fun deleteAppSettings(settings: List<AppSetting>) = withContext(dispatcher) {
        appSettingDao.delete(settings.map { appSettingToAppSettingEntityMapper.map(it) })
    }

    override suspend fun deleteAppSettings() = withContext(dispatcher) {
        appSettingDao.deleteAll()
    }
}

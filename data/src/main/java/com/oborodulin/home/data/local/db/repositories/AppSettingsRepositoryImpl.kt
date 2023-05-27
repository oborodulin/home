package com.oborodulin.home.data.local.db.repositories

import com.oborodulin.home.domain.model.AppSetting
import com.oborodulin.home.domain.repositories.AppSettingsRepository
import kotlinx.coroutines.flow.flow
import java.util.*
import javax.inject.Inject

class AppSettingsRepositoryImpl @Inject constructor(
    private val appSettingDataSource: AppSettingDataSource
) : AppSettingsRepository {
    override fun getAll() = appSettingDataSource.getAppSettings()

    override fun get(settingId: UUID) = appSettingDataSource.getAppSetting(settingId)

    override fun save(setting: AppSetting) = flow {
        appSettingDataSource.saveAppSetting(setting)
        emit(setting)
    }

    override fun delete(setting: AppSetting) = flow {
        appSettingDataSource.deleteAppSetting(setting)
        this.emit(setting)
    }

    override fun deleteById(settingId: UUID) = flow {
        appSettingDataSource.deleteAppSettingById(settingId)
        this.emit(settingId)
    }

    override suspend fun deleteAll() = appSettingDataSource.deleteAppSettings()
}
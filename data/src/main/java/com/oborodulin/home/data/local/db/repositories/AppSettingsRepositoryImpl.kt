package com.oborodulin.home.data.local.db.repositories

import com.oborodulin.home.data.local.db.mappers.AppSettingMappers
import com.oborodulin.home.data.local.db.repositories.sources.local.LocalAppSettingDataSource
import com.oborodulin.home.domain.model.AppSetting
import com.oborodulin.home.domain.repositories.AppSettingsRepository
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class AppSettingsRepositoryImpl @Inject constructor(
    private val localAppSettingDataSource: LocalAppSettingDataSource,
    private val mappers: AppSettingMappers
) : AppSettingsRepository {
    override fun getAll() =
        localAppSettingDataSource.getAppSettings()
            .map(mappers.appSettingEntityListToAppSettingListMapper::map)

    override fun get(settingId: UUID) =
        localAppSettingDataSource.getAppSetting(settingId)
            .map(mappers.appSettingEntityToAppSettingMapper::map)

    override fun save(setting: AppSetting) = flow {
        if (setting.id == null) {
            localAppSettingDataSource.insertAppSetting(
                mappers.appSettingToAppSettingEntityMapper.map(setting)
            )
        } else {
            localAppSettingDataSource.updateAppSetting(
                mappers.appSettingToAppSettingEntityMapper.map(setting)
            )
        }
        emit(setting)
    }

    override fun delete(setting: AppSetting) = flow {
        localAppSettingDataSource.deleteAppSetting(mappers.appSettingToAppSettingEntityMapper.map(setting))
        this.emit(setting)
    }

    override fun deleteById(settingId: UUID) = flow {
        localAppSettingDataSource.deleteAppSettingById(settingId)
        this.emit(settingId)
    }

    override fun delete(settings: List<AppSetting>) = flow {
        localAppSettingDataSource.deleteAppSettings(settings.map {
            mappers.appSettingToAppSettingEntityMapper.map(it)
        })
        this.emit(settings)
    }

    override suspend fun deleteAll() = localAppSettingDataSource.deleteAppSettings()
}
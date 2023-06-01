package com.oborodulin.home.data.local.db.repositories.sources.local

import com.oborodulin.home.data.local.db.entities.AppSettingEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface LocalAppSettingDataSource {
    fun getAppSettings(): Flow<List<AppSettingEntity>>
    fun getAppSetting(settingId: UUID): Flow<AppSettingEntity>
    suspend fun insertAppSetting(setting: AppSettingEntity)
    suspend fun updateAppSetting(setting: AppSettingEntity)
    suspend fun deleteAppSetting(setting: AppSettingEntity)
    suspend fun deleteAppSettingById(settingId: UUID)
    suspend fun deleteAppSettings(settings: List<AppSettingEntity>)
    suspend fun deleteAppSettings()
}
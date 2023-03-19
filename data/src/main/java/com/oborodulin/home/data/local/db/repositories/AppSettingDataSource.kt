package com.oborodulin.home.data.local.db.repositories

import com.oborodulin.home.domain.model.AppSetting
import kotlinx.coroutines.flow.Flow
import java.util.*

interface AppSettingDataSource {
    fun getAppSettings(): Flow<List<AppSetting>>
    fun getAppSetting(settingId: UUID): Flow<AppSetting>
    suspend fun saveAppSetting(setting: AppSetting)
    suspend fun deleteAppSetting(setting: AppSetting)
    suspend fun deleteAppSettingById(settingId: UUID)
    suspend fun deleteAppSettings(settings: List<AppSetting>)
    suspend fun deleteAppSettings()
}
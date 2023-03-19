package com.oborodulin.home.domain.repositories

import com.oborodulin.home.domain.model.AppSetting
import kotlinx.coroutines.flow.Flow
import java.util.*

interface AppSettingsRepository {
    fun getAll(): Flow<List<AppSetting>>
    fun get(settingId: UUID): Flow<AppSetting>
    fun save(setting: AppSetting): Flow<AppSetting>
    fun delete(setting: AppSetting): Flow<AppSetting>
    fun deleteById(settingId: UUID): Flow<UUID>
    suspend fun deleteAll()
}
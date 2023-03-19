package com.oborodulin.home.data.local.db.mappers

import com.oborodulin.home.common.mapping.Mapper
import com.oborodulin.home.data.local.db.entities.AppSettingEntity
import com.oborodulin.home.domain.model.AppSetting
import java.util.*

class AppSettingToAppSettingEntityMapper : Mapper<AppSetting, AppSettingEntity> {
    override fun map(input: AppSetting) = AppSettingEntity(
        settingId = input.id ?: input.apply { id = UUID.randomUUID() }.id!!,
        paramName = input.paramName,
        paramValue = input.paramValue
    )
}
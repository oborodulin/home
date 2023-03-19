package com.oborodulin.home.data.local.db.mappers

import com.oborodulin.home.common.mapping.Mapper
import com.oborodulin.home.data.local.db.entities.AppSettingEntity
import com.oborodulin.home.domain.model.AppSetting

class AppSettingEntityToAppSettingMapper : Mapper<AppSettingEntity, AppSetting> {
    override fun map(input: AppSettingEntity): AppSetting {
        val appSetting = AppSetting(
            paramName = input.paramName,
            paramValue = input.paramValue,
        )
        appSetting.id = input.settingId
        return appSetting
    }
}
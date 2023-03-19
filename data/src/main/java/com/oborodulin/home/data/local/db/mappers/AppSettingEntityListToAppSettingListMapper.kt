package com.oborodulin.home.data.local.db.mappers

import com.oborodulin.home.common.mapping.ListMapperImp
import com.oborodulin.home.data.local.db.entities.AppSettingEntity
import com.oborodulin.home.domain.model.AppSetting

class AppSettingEntityListToAppSettingListMapper(mapper: AppSettingEntityToAppSettingMapper) :
    ListMapperImp<AppSettingEntity, AppSetting>(mapper)
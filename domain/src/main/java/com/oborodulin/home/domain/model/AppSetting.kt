package com.oborodulin.home.domain.model

import com.oborodulin.home.common.domain.model.DomainModel
import com.oborodulin.home.domain.util.AppSettingParam

data class AppSetting(
    val paramName: AppSettingParam,
    val paramValue: String = ""
) : DomainModel()

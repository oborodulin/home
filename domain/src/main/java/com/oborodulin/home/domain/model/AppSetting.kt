package com.oborodulin.home.domain.model

data class AppSetting(
    val paramName: String = "",
    val paramValue: String = ""
) : DomainModel()

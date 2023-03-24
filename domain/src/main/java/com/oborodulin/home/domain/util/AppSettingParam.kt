package com.oborodulin.home.domain.util

enum class AppSettingParam {
    LANG,
    CURRENCY_CODE,
    PERSON_NUM_MU,
    TOTAL_AREA_MU,
    LIVING_SPACE_MU,
    HEATED_VOLUME_MU;

    fun dbVal() = "'" + this.name + "'"
}
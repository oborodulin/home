package com.oborodulin.home.accounting.ui.model

import com.oborodulin.home.data.local.db.entities.pojo.PrevServiceMeterValuePojo

data class AccountingModel(
    var prevServiceMeterVals: List<PrevServiceMeterValuePojo> = listOf(),
)

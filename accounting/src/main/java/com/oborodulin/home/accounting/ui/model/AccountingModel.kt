package com.oborodulin.home.accounting.ui.model

import com.oborodulin.home.metering.ui.model.MeterValueModel

data class AccountingModel(
    var serviceMeterVals: List<MeterValueModel> = listOf(),
)

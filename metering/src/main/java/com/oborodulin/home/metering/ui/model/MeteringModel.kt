package com.oborodulin.home.metering.ui.model

import com.oborodulin.home.domain.model.Payer
import com.oborodulin.home.metering.domain.model.Meter

data class MeteringModel(
    var payer: Payer,
    var meters: List<MeterListItemModel> = listOf()
)

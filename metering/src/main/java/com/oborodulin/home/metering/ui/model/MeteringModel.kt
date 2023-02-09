package com.oborodulin.home.metering.ui.model

import com.oborodulin.home.domain.model.Payer

data class MeteringModel(
    var payer: Payer,
    var meters: List<MeterListItem> = listOf()
)

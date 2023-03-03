package com.oborodulin.home.metering.ui.model

import com.oborodulin.home.domain.model.Payer

data class MeteringUi(
    val payer: Payer,
    val meters: List<MeterListItem> = listOf()
)

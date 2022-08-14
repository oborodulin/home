package com.oborodulin.home.accounting.payer

import com.oborodulin.home.domain.entity.Payer

data class PayerViewState(
    val payer: Payer = Payer(),
    val isLoading: Boolean,
    val error: String? = null
)
package com.oborodulin.home.accounting.ui.payer

import com.oborodulin.home.domain.model.Payer

data class PayerViewState(
    val payer: Payer = Payer(),
    val isLoading: Boolean,
    val error: String? = null
)
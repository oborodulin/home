package com.oborodulin.home.accounting.ui

import com.oborodulin.home.domain.model.Payer

data class AccountingScreenState(
    //val payerServiceMeters: List<Payer>,
    val payers: List<Payer>,
    val isLoading: Boolean,
    val error: String? = null
)

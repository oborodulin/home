package com.oborodulin.home.accounting

import com.oborodulin.home.domain.entity.Payer

data class AccountingScreenState(
    val payers: List<Payer>,
    val isLoading: Boolean,
    val error: String? = null
)

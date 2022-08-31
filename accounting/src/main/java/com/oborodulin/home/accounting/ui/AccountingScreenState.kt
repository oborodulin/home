package com.oborodulin.home.accounting.ui

import com.oborodulin.home.accounting.domain.model.Payer

data class AccountingScreenState(
    val payers: List<Payer>,
    val isLoading: Boolean,
    val error: String? = null
)

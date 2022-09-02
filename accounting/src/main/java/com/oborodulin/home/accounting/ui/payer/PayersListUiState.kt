package com.oborodulin.home.accounting.ui.payer

import com.oborodulin.home.accounting.domain.model.Payer

data class PayersListUiState(
    val payers: List<Payer>,
    val isLoading: Boolean,
    val error: String? = null
)

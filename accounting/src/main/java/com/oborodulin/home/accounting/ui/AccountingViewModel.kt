package com.oborodulin.home.accounting.ui

import com.oborodulin.home.accounting.ui.model.AccountingModel
import com.oborodulin.home.common.ui.state.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface AccountingViewModel {
    val uiStateFlow: StateFlow<UiState<AccountingModel>>
    val singleEventFlow : Flow<AccountingUiSingleEvent>

    fun submitAction(action: AccountingUiAction)
}
package com.oborodulin.home.accounting.ui.payer.list

import com.oborodulin.home.accounting.ui.model.PayerListItemModel
import com.oborodulin.home.common.ui.state.UiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface PayersListViewModel {
    val uiStateFlow: StateFlow<UiState<List<PayerListItemModel>>>
    val singleEventFlow: Flow<PayersListUiSingleEvent>

    fun submitAction(action: PayersListUiAction): Job?
}
package com.oborodulin.home.billing.ui.subtotals

import com.oborodulin.home.common.ui.state.UiState
import com.oborodulin.home.billing.ui.model.ServiceSubtotalListItem
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface PayerServiceSubtotalsListViewModel {
    var primaryObjectData: StateFlow<ArrayList<String>>

    val uiStateFlow: StateFlow<UiState<List<ServiceSubtotalListItem>>>
    val singleEventFlow: Flow<PayerServiceSubtotalsListUiSingleEvent>
    val actionsJobFlow: SharedFlow<Job?>

    fun submitAction(action: PayerServiceSubtotalsListUiAction): Job?
    fun handleActionJob(action: () -> Unit, afterAction: () -> Unit)
    fun setPrimaryObjectData(value: ArrayList<String>)
}
package com.oborodulin.home.accounting.ui.payer.list

import com.oborodulin.home.accounting.ui.model.PayerListItem
import com.oborodulin.home.common.ui.state.UiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.ArrayList

interface PayersListViewModel {
    var primaryObjectData: StateFlow<ArrayList<String>>

    val uiStateFlow: StateFlow<UiState<List<PayerListItem>>>
    val singleEventFlow: Flow<PayersListUiSingleEvent>
    val actionsJobFlow: SharedFlow<Job?>

    fun submitAction(action: PayersListUiAction): Job?
    fun handleActionJob(action: () -> Unit, afterAction: () -> Unit)
    fun setPrimaryObjectData(value: ArrayList<String>)
}
package com.oborodulin.home.servicing.ui.rate.list

import com.oborodulin.home.servicing.ui.model.RateListItem
import com.oborodulin.home.common.ui.state.UiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface RatesListViewModel {
    var primaryObjectData: StateFlow<ArrayList<String>>

    val uiStateFlow: StateFlow<UiState<List<RateListItem>>>
    val singleEventFlow: Flow<RatesListUiSingleEvent>
    val actionsJobFlow: SharedFlow<Job?>

    fun submitAction(action: RatesListUiAction): Job?
    fun handleActionJob(action: () -> Unit, afterAction: () -> Unit)
    fun setPrimaryObjectData(value: ArrayList<String>)
}
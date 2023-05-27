package com.oborodulin.home.servicing.ui.service.list

import com.oborodulin.home.common.ui.state.UiState
import com.oborodulin.home.servicing.ui.model.ServiceListItem
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface ServicesListViewModel {
    var primaryObjectData: StateFlow<ArrayList<String>>

    val uiStateFlow: StateFlow<UiState<List<ServiceListItem>>>
    val singleEventFlow: Flow<ServicesListUiSingleEvent>
    val actionsJobFlow: SharedFlow<Job?>

    fun submitAction(action: ServicesListUiAction): Job?
    fun handleActionJob(action: () -> Unit, afterAction: () -> Unit)
    fun setPrimaryObjectData(value: ArrayList<String>)
}
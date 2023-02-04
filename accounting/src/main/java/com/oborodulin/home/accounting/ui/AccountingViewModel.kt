package com.oborodulin.home.accounting.ui

import androidx.compose.runtime.MutableState
import com.oborodulin.home.accounting.ui.model.AccountingModel
import com.oborodulin.home.common.ui.state.UiState
import com.oborodulin.home.metering.ui.model.MeterValueListItemModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface AccountingViewModel {
    val uiStateFlow: StateFlow<UiState<AccountingModel>>
    val singleEventFlow : Flow<AccountingUiSingleEvent>
//    val uiMeterValuesState: MutableState<List<MeterValueListItemModel>>

    fun submitAction(action: AccountingUiAction): Job?
}
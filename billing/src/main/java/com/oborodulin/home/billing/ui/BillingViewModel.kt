package com.oborodulin.home.billing.ui

import com.oborodulin.home.accounting.ui.model.AccountingUi
import com.oborodulin.home.common.ui.state.UiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface BillingViewModel {
    val uiStateFlow: StateFlow<UiState<AccountingUi>>
    val singleEventFlow : Flow<AccountingUiSingleEvent>
//    val uiMeterValuesState: MutableState<List<MeterValueListItem>>

    fun submitAction(action: AccountingUiAction): Job?
}
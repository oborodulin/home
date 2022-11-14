package com.oborodulin.home.accounting.ui

import com.oborodulin.home.common.ui.state.UiAction
import java.util.*

sealed class AccountingUiAction : UiAction {
    object Load : AccountingUiAction()
    object LoadPrevServiceMeterVals : AccountingUiAction()
    data class PayerClick(val payerId: UUID) : AccountingUiAction()
    data class EditPayer(val payerId: UUID) : AccountingUiAction()
}


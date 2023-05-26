package com.oborodulin.home.billing.ui

import com.oborodulin.home.common.ui.state.UiAction
import java.util.*

sealed class BillingUiAction : UiAction {
    object Init : BillingUiAction()
    //data class Load(val payerId: UUID) : AccountingUiAction()
}


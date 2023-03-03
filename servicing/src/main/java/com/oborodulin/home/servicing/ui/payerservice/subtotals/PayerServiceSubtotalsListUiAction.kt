package com.oborodulin.home.servicing.ui.payerservice.subtotals

import com.oborodulin.home.common.ui.state.UiAction
import java.util.*

sealed class PayerServiceSubtotalsListUiAction : UiAction {
    data class Load(val payerId: UUID) : PayerServiceSubtotalsListUiAction()
}
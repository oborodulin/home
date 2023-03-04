package com.oborodulin.home.billing.ui.subtotals

import com.oborodulin.home.common.ui.state.UiAction
import java.util.*

sealed class PayerServiceSubtotalsListUiAction : UiAction {
    data class Load(val payerId: UUID) : PayerServiceSubtotalsListUiAction()
}
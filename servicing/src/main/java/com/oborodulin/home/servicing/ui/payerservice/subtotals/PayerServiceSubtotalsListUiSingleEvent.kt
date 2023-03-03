package com.oborodulin.home.servicing.ui.payerservice.subtotals

import com.oborodulin.home.common.ui.state.UiSingleEvent

sealed class PayerServiceSubtotalsListUiSingleEvent : UiSingleEvent {
    data class OpenPayerServiceScreen(val navRoute: String) : PayerServiceSubtotalsListUiSingleEvent()
}


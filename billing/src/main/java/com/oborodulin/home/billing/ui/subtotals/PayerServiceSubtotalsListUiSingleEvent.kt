package com.oborodulin.home.billing.ui.subtotals

import com.oborodulin.home.common.ui.state.UiSingleEvent

sealed class PayerServiceSubtotalsListUiSingleEvent : UiSingleEvent {
    data class OpenPayerServiceScreen(val navRoute: String) : PayerServiceSubtotalsListUiSingleEvent()
}


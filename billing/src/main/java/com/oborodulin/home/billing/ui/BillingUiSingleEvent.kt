package com.oborodulin.home.billing.ui

import com.oborodulin.home.common.ui.state.UiSingleEvent

sealed class BillingUiSingleEvent : UiSingleEvent {
    data class OpenPayerScreen(val navRoute: String) : BillingUiSingleEvent()
}


package com.oborodulin.home.accounting.ui

import com.oborodulin.home.common.ui.state.UiSingleEvent

sealed class AccountingUiSingleEvent : UiSingleEvent {
    data class OpenPayerScreen(val navRoute: String) : AccountingUiSingleEvent()
}


package com.oborodulin.home.accounting.ui

import com.oborodulin.home.common.ui.state.UiSingleEvent

sealed class AccountingUiSingleEvent : UiSingleEvent {
    data class OpenPayerDetailScreen(val navRoute: String) : AccountingUiSingleEvent()
}


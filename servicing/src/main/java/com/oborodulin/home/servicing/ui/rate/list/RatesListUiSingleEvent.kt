package com.oborodulin.home.servicing.ui.rate.list

import com.oborodulin.home.common.ui.state.UiSingleEvent

sealed class RatesListUiSingleEvent : UiSingleEvent {
    data class OpenPayerScreen(val navRoute: String) : RatesListUiSingleEvent()
}

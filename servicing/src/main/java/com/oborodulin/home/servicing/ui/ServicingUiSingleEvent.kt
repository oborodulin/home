package com.oborodulin.home.servicing.ui

import com.oborodulin.home.common.ui.state.UiSingleEvent

sealed class ServicingUiSingleEvent : UiSingleEvent {
    data class OpenPayerScreen(val navRoute: String) : ServicingUiSingleEvent()
}


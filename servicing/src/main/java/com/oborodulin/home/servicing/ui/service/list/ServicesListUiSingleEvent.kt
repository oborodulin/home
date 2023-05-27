package com.oborodulin.home.servicing.ui.service.list

import com.oborodulin.home.common.ui.state.UiSingleEvent

sealed class ServicesListUiSingleEvent : UiSingleEvent {
    data class OpenServiceScreen(val navRoute: String) : ServicesListUiSingleEvent()
}


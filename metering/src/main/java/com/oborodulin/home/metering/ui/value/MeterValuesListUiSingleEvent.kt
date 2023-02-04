package com.oborodulin.home.metering.ui.value

import com.oborodulin.home.common.ui.state.UiSingleEvent

sealed class MeterValuesListUiSingleEvent : UiSingleEvent {
    data class OpenPhotoScreen(val navRoute: String) : MeterValuesListUiSingleEvent()
}


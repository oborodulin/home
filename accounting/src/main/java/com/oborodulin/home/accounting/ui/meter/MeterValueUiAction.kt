package com.oborodulin.home.accounting.ui.meter

import com.oborodulin.home.common.ui.state.UiAction

sealed class MeterValueUiAction : UiAction {
    object Save : MeterValueUiAction()
}
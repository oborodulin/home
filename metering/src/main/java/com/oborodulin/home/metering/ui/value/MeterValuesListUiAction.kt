package com.oborodulin.home.metering.ui.value

import com.oborodulin.home.common.ui.state.UiAction
import java.util.*

sealed class MeterValuesListUiAction : UiAction {
    object Init : MeterValuesListUiAction()
    data class Load(val payerId: UUID) : MeterValuesListUiAction()
    data class Delete(val meterValueId: UUID) : MeterValuesListUiAction()
    object Save : MeterValuesListUiAction()
}
package com.oborodulin.home.servicing.ui.rate.list

import com.oborodulin.home.common.ui.state.UiAction
import java.util.UUID

sealed class RatesListUiAction : UiAction {
    object Load : RatesListUiAction()
    data class EditRate(val rateId: UUID) : RatesListUiAction()
    data class DeleteRate(val rateId: UUID) : RatesListUiAction()
}
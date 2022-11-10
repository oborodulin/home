package com.oborodulin.home.accounting.ui.payer.list

import com.oborodulin.home.common.ui.state.UiAction
import java.util.*

sealed class PayersListUiAction : UiAction {
    object Load : PayersListUiAction()
    data class PayerClick(val payerId: UUID) : PayersListUiAction()
}


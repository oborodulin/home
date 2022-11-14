package com.oborodulin.home.accounting.ui.payer.list

import com.oborodulin.home.common.ui.state.UiAction
import java.util.*

sealed class PayersListUiAction : UiAction {
    object Load : PayersListUiAction()
    data class EditPayer(val payerId: UUID) : PayersListUiAction()
    data class DeletePayer(val payerId: UUID) : PayersListUiAction()
}


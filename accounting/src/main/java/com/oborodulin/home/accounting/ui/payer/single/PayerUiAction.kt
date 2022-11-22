package com.oborodulin.home.accounting.ui.payer.single

import com.oborodulin.home.common.ui.state.UiAction
import java.util.*

sealed class PayerUiAction : UiAction {
    object Create : PayerUiAction()
    data class Load(val payerId: UUID) : PayerUiAction()
    object Save : PayerUiAction()
//    data class ShowCompletedTasks(val show: Boolean) : PayersListEvent()
//    data class ChangeSortByPriority(val enable: Boolean) : PayersListEvent()
//    data class ChangeSortByDeadline(val enable: Boolean) : PayersListEvent()
}
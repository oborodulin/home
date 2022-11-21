package com.oborodulin.home.accounting.ui.payer.single

import com.oborodulin.home.accounting.ui.model.PayerModel
import com.oborodulin.home.common.ui.state.UiAction
import java.util.*

sealed class PayerUiAction : UiAction {
    data class Load(val payerId: UUID) : PayerUiAction()
    data class Save(val payerModel: PayerModel) : PayerUiAction()
//    data class ShowCompletedTasks(val show: Boolean) : PayersListEvent()
//    data class ChangeSortByPriority(val enable: Boolean) : PayersListEvent()
//    data class ChangeSortByDeadline(val enable: Boolean) : PayersListEvent()
}
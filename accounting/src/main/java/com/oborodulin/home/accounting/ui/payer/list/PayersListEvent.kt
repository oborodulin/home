package com.oborodulin.home.accounting.ui.payer.list

import com.oborodulin.home.accounting.domain.model.Payer

sealed class PayersListEvent {
    data class DeletePayer(val payer: Payer) : PayersListEvent()
//    data class ShowCompletedTasks(val show: Boolean) : PayersListEvent()
//    data class ChangeSortByPriority(val enable: Boolean) : PayersListEvent()
//    data class ChangeSortByDeadline(val enable: Boolean) : PayersListEvent()
}

package com.oborodulin.home.servicing.ui.rate.list

import com.oborodulin.home.common.ui.state.UiAction
import java.util.*

sealed class RatesListUiAction : UiAction {
    object Load : RatesListUiAction()
    data class EditPayer(val payerId: UUID) : RatesListUiAction()
    data class DeletePayer(val payerId: UUID) : RatesListUiAction()
    data class FavoritePayer(val payerId: UUID) : RatesListUiAction()
//    data class ShowCompletedTasks(val show: Boolean) : PayersListEvent()
//    data class ChangeSortByPriority(val enable: Boolean) : PayersListEvent()
//    data class ChangeSortByDeadline(val enable: Boolean) : PayersListEvent()
}
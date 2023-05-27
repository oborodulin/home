package com.oborodulin.home.servicing.ui.service.list

import com.oborodulin.home.common.ui.state.UiAction
import java.util.UUID

sealed class ServicesListUiAction : UiAction {
    object Load : ServicesListUiAction()
    data class EditService(val serviceId: UUID) : ServicesListUiAction()
    data class DeleteService(val serviceId: UUID) : ServicesListUiAction()
}
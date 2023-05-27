package com.oborodulin.home.servicing.ui

import com.oborodulin.home.common.ui.state.UiAction

sealed class ServicingUiAction : UiAction {
    object Init : ServicingUiAction()
    //data class Load(val payerId: UUID) : AccountingUiAction()
}


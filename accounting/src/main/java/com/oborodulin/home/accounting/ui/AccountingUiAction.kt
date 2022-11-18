package com.oborodulin.home.accounting.ui

import com.oborodulin.home.common.ui.state.UiAction
import java.util.*

sealed class AccountingUiAction : UiAction {
    data class Load(val payerId: UUID? = null) : AccountingUiAction()
}


package com.oborodulin.home.common.ui.state

import kotlinx.coroutines.flow.StateFlow

interface DialogViewModel {
    val showDialog: StateFlow<Boolean>

    fun onOpenDialogClicked()
    fun onDialogConfirm(onConfirm: () -> Unit)
    fun onDialogDismiss(onDismiss: () -> Unit = {})
}
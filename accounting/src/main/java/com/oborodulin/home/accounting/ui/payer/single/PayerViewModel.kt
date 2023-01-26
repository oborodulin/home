package com.oborodulin.home.accounting.ui.payer.single

import com.oborodulin.home.common.ui.components.field.Focusable
import com.oborodulin.home.common.ui.components.field.InputWrapper
import com.oborodulin.home.common.ui.components.field.Inputable
import com.oborodulin.home.common.ui.components.field.ScreenEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface PayerViewModel {
    val events: Flow<ScreenEvent>
    val ercCode: StateFlow<InputWrapper>
    val fullName: StateFlow<InputWrapper>
    val address: StateFlow<InputWrapper>
    val totalArea: StateFlow<InputWrapper>
    val livingSpace: StateFlow<InputWrapper>
    val heatedVolume: StateFlow<InputWrapper>

    val areInputsValid: StateFlow<Boolean>

    fun onTextFieldEntered(inputEvent: Inputable)
    fun onTextFieldFocusChanged(focusedField: Focusable, isFocused: Boolean)
    fun moveFocusImeAction()
    fun onContinueClick(onSuccess: () -> Unit)
}
package com.oborodulin.home.accounting.ui.payer.single

import com.oborodulin.home.common.ui.components.field.util.InputWrapper
import com.oborodulin.home.common.ui.components.field.util.Inputable
import com.oborodulin.home.common.ui.components.field.util.ScreenEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface PayerViewModel {
    val events: Flow<ScreenEvent>
    val actionsJobFlow: SharedFlow<Job?>

    val ercCode: StateFlow<InputWrapper>
    val fullName: StateFlow<InputWrapper>
    val address: StateFlow<InputWrapper>
    val totalArea: StateFlow<InputWrapper>
    val livingSpace: StateFlow<InputWrapper>
    val heatedVolume: StateFlow<InputWrapper>
    val paymentDay: StateFlow<InputWrapper>
    val personsNum: StateFlow<InputWrapper>

    val areInputsValid: StateFlow<Boolean>

    fun viewModelScope(): CoroutineScope
    fun onTextFieldEntered(inputEvent: Inputable)
    fun onTextFieldFocusChanged(focusedField: PayerFields, isFocused: Boolean)
    fun moveFocusImeAction()
    fun onContinueClick(onSuccess: () -> Unit)
}
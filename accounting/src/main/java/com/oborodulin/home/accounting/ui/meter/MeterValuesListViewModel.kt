package com.oborodulin.home.accounting.ui.meter

import com.oborodulin.home.common.ui.components.field.util.Inputable
import com.oborodulin.home.common.ui.components.field.util.InputsWrapper
import com.oborodulin.home.common.ui.components.field.util.ScreenEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface MeterValuesListViewModel {
    val events: Flow<ScreenEvent>
    val currentValue: StateFlow<InputsWrapper>
    val areInputsValid: StateFlow<Boolean>

    fun initFieldStatesByUiModel(uiModel: Any): Job?
    fun onTextFieldEntered(inputEvent: Inputable)
    fun onTextFieldFocusChanged(
        focusedField: MeterValueFields, isFocused: Boolean,
        onFocusIn: () -> Unit, onFocusOut: () -> Unit
    )
    fun clearInputFieldsStates()
    fun onContinueClick(onSuccess: () -> Unit)
    fun submitAction(action: MeterValuesListUiAction): Job?
}
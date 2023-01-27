package com.oborodulin.home.accounting.ui.meter

import com.oborodulin.home.common.ui.components.field.Focusable
import com.oborodulin.home.common.ui.components.field.InputWrapper
import com.oborodulin.home.common.ui.components.field.Inputable
import com.oborodulin.home.common.ui.components.field.ScreenEvent
import com.oborodulin.home.metering.ui.model.MeterValueModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface MeterValueViewModel {
    val events: Flow<ScreenEvent>
    val currentValue: StateFlow<InputWrapper>
    val areInputsValid: StateFlow<Boolean>

    fun initFieldStatesByUiModel(uiModel: Any): Job?
    fun onTextFieldEntered(inputEvent: Inputable)
    fun onTextFieldFocusChanged(focusedField: Focusable, isFocused: Boolean)
    fun onContinueClick(onSuccess: () -> Unit)
    fun submitAction(action: MeterValueUiAction): Job?
}
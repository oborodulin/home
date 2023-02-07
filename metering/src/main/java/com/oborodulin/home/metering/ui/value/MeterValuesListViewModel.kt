package com.oborodulin.home.metering.ui.value

import com.oborodulin.home.common.ui.components.field.util.Inputable
import com.oborodulin.home.common.ui.components.field.util.InputsWrapper
import com.oborodulin.home.common.ui.components.field.util.ScreenEvent
import com.oborodulin.home.common.ui.state.UiState
import com.oborodulin.home.metering.ui.model.MeterValueListItemModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface MeterValuesListViewModel {
    val uiStateFlow: StateFlow<UiState<List<MeterValueListItemModel>>>
    val singleEventFlow: Flow<MeterValuesListUiSingleEvent>
    val events: Flow<ScreenEvent>
    val currentValue: StateFlow<InputsWrapper>
    val areInputsValid: StateFlow<Boolean>

    fun initFieldStatesByUiModel(uiModel: Any): Job?
    fun setStateValue(
        field: MeterValueFields, properties: StateFlow<InputsWrapper>, value: String, key: String,
        isValid: Boolean, isSaved: Boolean
    )
    fun onTextFieldEntered(inputEvent: Inputable)
    fun onTextFieldFocusChanged(
        focusedField: MeterValueFields, isFocused: Boolean,
        onFocusIn: () -> Unit, onFocusOut: () -> Unit
    )
    fun clearInputFieldsStates()
    fun onContinueClick(onSuccess: () -> Unit)
    fun submitAction(action: MeterValuesListUiAction): Job?
}
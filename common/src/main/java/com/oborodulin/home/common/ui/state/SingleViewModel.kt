package com.oborodulin.home.common.ui.state

import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.oborodulin.home.common.ui.components.field.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber

private const val TAG = "Common.SingleViewModel"
private const val FOCUSED_FIELD_KEY = "focusedTextField"

abstract class SingleViewModel<T : Any, S : UiState<T>, A : UiAction, E : UiSingleEvent, F : Focusable, W : InputWrapped>(
    private val state: SavedStateHandle,
    private val initFocusedTextField: Focusable? = null,
) : MviViewModel<T, S, A, E>(state = state) {

    private var focusedTextField = FocusedTextField(
        textField = initFocusedTextField,
        key = state[FOCUSED_FIELD_KEY] ?: initFocusedTextField?.key()
    )
        set(value) {
            field = value
            state[FOCUSED_FIELD_KEY] = value.key
        }

    private val _events = Channel<ScreenEvent>()
    val events = _events.receiveAsFlow()
    val inputEvents = Channel<Inputable>(Channel.CONFLATED)

    init {
        Timber.tag(TAG).d("init: Start observe input events")
        //Dispatchers.Default
        viewModelScope.launch(errorHandler) {
            observeInputEvents()
        }
        focusedTextField.key?.let {
            focusOnLastSelectedTextField()
        }
    }

    override fun initFieldStatesByUiModel(uiModel: Any): Job? = null

    abstract suspend fun observeInputEvents()


    fun initStateValue(field: F, property: StateFlow<InputWrapper>, value: String) {
        Timber.tag(TAG)
            .d("initStateValue(...): exist state %s = '%s'", field.key(), state[field.key()])
        if (property.value.isEmpty) {
            Timber.tag(TAG).d("initStateValue(...): %s = '%s'", field.key(), value)
            setStateValue(field, property, value)
        }
    }

    fun setStateValue(
        field: F, property: StateFlow<InputWrapper>, value: String, isValid: Boolean = false
    ) {
        Timber.tag(TAG)
            .d("setStateValue(...): %s = '%s' [valid = %s]", field.key(), value, isValid)
        if (isValid) {
            state[field.key()] = property.value.copy(value = value, errorId = null, isEmpty = false)
        } else {
            state[field.key()] = property.value.copy(value = value, isEmpty = false)
        }
    }

    fun setStateValue(field: F, property: StateFlow<InputWrapper>, @StringRes errorId: Int?) {
        Timber.tag(TAG)
            .d("setStateValue(...): Validate (debounce) %s - ERR[%s]", field.key(), errorId)
        state[field.key()] = property.value.copy(errorId = errorId, isEmpty = false)
    }

    fun initStateValue(
        field: F, properties: StateFlow<InputsWrapper>, value: String, key: String
    ) {
        Timber.tag(TAG)
            .d("initStateValue(...): exist state %s = '%s'", field.key(), state[field.key()])
        Timber.tag(TAG)
            .d(
                "initStateValue(...): properties.value.inputs[%s] = '%s'",
                key, properties.value.inputs[key]
            )
        if (state.get<T>(field.key()) == null || properties.value.inputs[key] == null || properties.value.inputs[key]?.isEmpty == true) {
            properties.value.inputs[key] = InputWrapper(value = value, isEmpty = false)
            Timber.tag(TAG)
                .d(
                    "initStateValue(...): add init value %s = '%s'",
                    field.key(),
                    properties.value.inputs[key]
                )
            Timber.tag(TAG)
                .d("initStateValue(...): copy %s = '%s'", field.key(), properties.value.inputs)
            state[field.key()] =
                properties.value.copy(inputs = properties.value.inputs.toMutableMap())
        }
    }

    fun setStateValue(
        field: F, properties: StateFlow<InputsWrapper>, value: String, key: String,
        isValid: Boolean = false, isSaved: Boolean = false
    ) {
        Timber.tag(TAG)
            .d(
                "setStateValue(...): %s = '%s' [valid = %s; isSaved = %s]", field.key(),
                value, isValid, isSaved
            )
        if (isValid) {
            properties.value.inputs[key] = InputWrapper(
                value = value, errorId = null, isEmpty = false, isSaved = isSaved
            )
        } else {
            properties.value.inputs[key] = InputWrapper(
                value = value, isEmpty = false, isSaved = true
            )
        }
        state[field.key()] = properties.value.copy(inputs = properties.value.inputs.toMutableMap())
    }

    fun setStateValue(
        field: F, properties: StateFlow<InputsWrapper>, key: String, @StringRes errorId: Int?
    ) {
        Timber.tag(TAG)
            .d("setStateValue(...): Validate (debounce) %s - ERR[%s]", field.key(), errorId)
        val property = properties.value.inputs.getValue(key)
        properties.value.inputs[key] =
            InputWrapper(
                value = property.value,
                errorId = errorId,
                isEmpty = false,
                isSaved = errorId != null
            )
        state[field.key()] = properties.value.copy(inputs = properties.value.inputs.toMutableMap())
    }

    fun onTextFieldEntered(inputEvent: Inputable) {
        Timber.tag(TAG).d("onTextFieldEntered: %s", inputEvent.javaClass.name)
        inputEvents.trySend(inputEvent)
    }

    fun onTextFieldFocusChanged(focusedField: F, isFocused: Boolean) {
        Timber.tag(TAG)
            .d("onTextFieldFocusChanged: %s - %s", focusedField.key(), isFocused)
        focusedTextField.key = if (isFocused) focusedField.key() else null
    }

    fun moveFocusImeAction() {
        Timber.tag(TAG).d("moveFocusImeAction() called")
        _events.trySend(ScreenEvent.MoveFocus())
    }

    fun onContinueClick(onSuccess: () -> Unit) {
        Timber.tag(TAG).d("onContinueClick(onSuccess) called")
        viewModelScope.launch(Dispatchers.Default) {
            when (val inputErrors = getInputErrorsOrNull()) {
                null -> {
                    clearFocusAndHideKeyboard()
                    onSuccess()
                    clearInputFieldsStates()
                    //_events.send(ScreenEvent.ShowToast(com.oborodulin.home.common.R.string.success))
                }
                else -> displayInputErrors(inputErrors)
            }
        }
    }

    abstract fun getInputErrorsOrNull(): List<InputError>?

    abstract fun displayInputErrors(inputErrors: List<InputError>)

    abstract fun stateInputFields(): List<String>

    open fun clearInputFieldsStates() {
        stateInputFields().forEach {
            Timber.tag(TAG).d("clearInputFieldsStates(): remove state '%s'", it)
            state.remove<W>(it)
        }
    }

    private suspend fun clearFocusAndHideKeyboard() {
        Timber.tag(TAG).d("clearFocusAndHideKeyboard() called")
        _events.send(ScreenEvent.ClearFocus)
        _events.send(ScreenEvent.UpdateKeyboard(false))
        focusedTextField.textField = null
        focusedTextField.key = null
    }

    private fun focusOnLastSelectedTextField() {
        Timber.tag(TAG).d("focusOnLastSelectedTextField() called")
        viewModelScope.launch(Dispatchers.Default) {
            focusedTextField.textField?.let {
                _events.send(ScreenEvent.RequestFocus(focusedTextField.textField!!))
                delay(250)
                _events.send(ScreenEvent.UpdateKeyboard(true))
            }
        }
    }
}
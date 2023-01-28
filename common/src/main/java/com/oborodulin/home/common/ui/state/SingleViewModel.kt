package com.oborodulin.home.common.ui.state

import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.oborodulin.home.common.ui.components.field.util.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import timber.log.Timber

private const val TAG = "Common.SingleViewModel"

abstract class SingleViewModel<T : Any, S : UiState<T>, A : UiAction, E : UiSingleEvent>(
    private val state: SavedStateHandle,
    private val initFocusedTextField: Focusable? = null,
) : MviViewModel<T, S, A, E>() {
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

    private val errorHandler = CoroutineExceptionHandler { _, exception ->
        Timber.tag(TAG).e(exception)
    }

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

    fun initStateValue(field: Focusable, property: StateFlow<InputWrapper>, value: String) {
        Timber.tag(TAG).d(
            "initStateValue(...): exists state %s = '%s'", field.key(),
            state[field.key()]
        )
        if (property.value.isEmpty) {
            Timber.tag(TAG).d("initStateValue(...): %s = '%s'", field.key(), value)
            setStateValue(field, property, value)
        }
    }

    fun setStateValue(field: Focusable, property: StateFlow<InputWrapper>, value: String) {
        Timber.tag(TAG).d("setStateValue(...): %s = '%s'", field.key(), value)
        state[field.key()] = property.value.copy(value = value, isEmpty = false)
    }

    fun setStateValue(
        field: Focusable,
        property: StateFlow<InputWrapper>,
        @StringRes errorId: Int?
    ) {
        Timber.tag(TAG)
            .d("setStateValue(...): Validate (debounce) %s - ERR[%s]", field.key(), errorId)
        state[field.key()] = property.value.copy(errorId = errorId, isEmpty = false)
    }

    fun setStateValidValue(field: Focusable, property: StateFlow<InputWrapper>, value: String) {
        Timber.tag(TAG).d("setStateValidValue(...): %s = '%s'", field.key(), value)
        state[field.key()] = property.value.copy(value = value, errorId = null, isEmpty = false)
    }

    fun onTextFieldEntered(inputEvent: Inputable) {
        Timber.tag(TAG).d("onTextFieldEntered: %s", inputEvent.javaClass.name)
        inputEvents.trySend(inputEvent)
    }

    fun onTextFieldFocusChanged(focusedField: Focusable, isFocused: Boolean) {
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
                    stateInputFields().forEach {
                        Timber.tag(TAG).d("onContinueClick(onSuccess): remove state '%s'", it)
                        state.remove<InputWrapper>(it)
                    }
                    //_events.send(ScreenEvent.ShowToast(com.oborodulin.home.common.R.string.success))
                }
                else -> displayInputErrors(inputErrors)
            }
        }
    }

    abstract fun getInputErrorsOrNull(): List<InputError>?

    abstract fun displayInputErrors(inputErrors: List<InputError>)

    abstract fun stateInputFields(): List<String>

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
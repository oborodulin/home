package com.oborodulin.home.common.ui.state

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.oborodulin.home.common.ui.components.*
import com.oborodulin.home.common.ui.components.field.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

private const val TAG = "Accounting.ui.SingleViewModel"

@OptIn(FlowPreview::class)
abstract class SingleViewModel<T : Any, S : UiState<T>, A : UiAction, E : UiSingleEvent>(
    private val handle: SavedStateHandle,
    private val initFocusedTextField: Focusable,
) : MviViewModel<T, S, A, E>() {
    private var focusedTextField = FocusedTextField(
        initFocusedTextField,
        handle["focusedTextField"] ?: initFocusedTextField.key()
    )
        set(value) {
            field = value
            handle["focusedTextField"] = value.key
        }

    private val _events = Channel<ScreenEvent>()
    val events = _events.receiveAsFlow()
    val inputEvents = Channel<Inputable>(Channel.CONFLATED)

    init {
        viewModelScope.launch(Dispatchers.Default) {
            observeInputEvents()
        }
        focusedTextField.key?.let {
            focusOnLastSelectedTextField()
        }
    }

    abstract suspend fun observeInputEvents()

    fun onTextFieldEntered(inputEvent: Inputable) {
        inputEvents.trySend(inputEvent)
    }

    fun onTextFieldFocusChanged(focusedField: Focusable, isFocused: Boolean) {
        focusedTextField.key = if (isFocused) focusedField.key() else null
    }

    fun moveFocusImeAction() {
        _events.trySend(ScreenEvent.MoveFocus())
    }

    fun onContinueClick(onSuccess: ) {
        viewModelScope.launch(Dispatchers.Default) {
            when (val inputErrors = getInputErrorsOrNull()) {
                null -> {
                    clearFocusAndHideKeyboard()
                    _events.send(ScreenEvent.ShowToast(com.oborodulin.home.common.R.string.success))
                }
                else -> displayInputErrors(inputErrors)
            }
        }
    }

    abstract fun getInputErrorsOrNull(): List<InputError>?

    abstract fun displayInputErrors(inputErrors: List<InputError>)

    private suspend fun clearFocusAndHideKeyboard() {
        _events.send(ScreenEvent.ClearFocus)
        _events.send(ScreenEvent.UpdateKeyboard(false))
        focusedTextField.textField = null
        focusedTextField.key = null
    }

    private fun focusOnLastSelectedTextField() {
        viewModelScope.launch(Dispatchers.Default) {
            focusedTextField.textField?.let {
                _events.send(ScreenEvent.RequestFocus(focusedTextField.textField!!))
                delay(250)
                _events.send(ScreenEvent.UpdateKeyboard(true))
            }
        }
    }
}
package com.oborodulin.home.common.ui.state

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.oborodulin.home.common.ui.components.*
import com.oborodulin.home.common.ui.components.field.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import timber.log.Timber
import java.util.*

private const val TAG = "Common.SingleViewModel"
private const val FOCUSED_FIELD_KEY = "focusedTextField"

@OptIn(FlowPreview::class)
abstract class SingleViewModel<T : Any, S : UiState<T>, A : UiAction, E : UiSingleEvent>(
    private val state: SavedStateHandle,
    private val initFocusedTextField: Focusable,
) : MviViewModel<T, S, A, E>() {
    private var focusedTextField = FocusedTextField(
        textField = initFocusedTextField,
        key = state[FOCUSED_FIELD_KEY] ?: initFocusedTextField.key()
    )
        set(value) {
            field = value
            state[FOCUSED_FIELD_KEY] = value.key
        }

    private val _events = Channel<ScreenEvent>()
    val events = _events.receiveAsFlow()
    val inputEvents = Channel<Inputable>(Channel.CONFLATED)

    private val errorHandler = CoroutineExceptionHandler { _, exception ->
        Timber.tag(TAG).e(exception, exception.message)
        //_uiState.value = _uiState.value.copy(error = exception.message, isLoading = false)
    }

    init {
        Timber.tag(TAG).d("init: Start observe input events")
        viewModelScope.launch(Dispatchers.Default) {
            observeInputEvents()
        }
        focusedTextField.key?.let {
            focusOnLastSelectedTextField()
        }
    }

    override fun initFieldStatesByUiModel(uiModel: Any) {}

    abstract suspend fun observeInputEvents()

    fun onTextFieldEntered(inputEvent: Inputable) {
        Timber.tag(TAG).d("onTextFieldEntered: %s".format(inputEvent.javaClass.name))
        inputEvents.trySend(inputEvent)
    }

    fun onTextFieldFocusChanged(focusedField: Focusable, isFocused: Boolean) {
        Timber.tag(TAG)
            .d("onTextFieldFocusChanged: %s - %s".format(focusedField.javaClass.name, isFocused))
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
                    //_events.send(ScreenEvent.ShowToast(com.oborodulin.home.common.R.string.success))
                }
                else -> displayInputErrors(inputErrors)
            }
        }
    }

    abstract fun getInputErrorsOrNull(): List<InputError>?

    abstract fun displayInputErrors(inputErrors: List<InputError>)

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
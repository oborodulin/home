package com.oborodulin.home.common.ui.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oborodulin.home.common.domain.entities.Result
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber

private const val TAG = "Common.MviViewModel"

abstract class MviViewModel<T : Any, S : UiState<T>, A : UiAction, E : UiSingleEvent> :
    ViewModel() {
    private val _uiStateFlow: MutableStateFlow<S> by lazy { MutableStateFlow(initState()) }
    val uiStateFlow: StateFlow<S> = _uiStateFlow

    private val actionFlow: MutableSharedFlow<A> = MutableSharedFlow()

    private val _singleEventFlow = Channel<E>()
    val singleEventFlow = _singleEventFlow.receiveAsFlow()

    private val errorHandler = CoroutineExceptionHandler { _, exception ->
        Timber.tag(TAG).e(exception, exception.message)
        //_uiState.value = _uiState.value.copy(error = exception.message, isLoading = false)
    }

    init {
        Timber.tag(TAG).d("init")
        viewModelScope.launch(errorHandler) {
            Timber.tag(TAG).d("init: Start actionFlow.collect")
            actionFlow.collect {
                handleAction(it)
            }
        }
    }

    abstract fun initState(): S

    abstract fun handleAction(action: A)

    abstract fun initFieldStatesByUiModel(uiModel: Any)

    fun submitAction(action: A) {
        Timber.tag(TAG).d("submitAction: emit action = %s", action.javaClass.name)
        viewModelScope.launch(errorHandler) {
            actionFlow.emit(action)
        }
    }

    fun submitState(state: S) {
        Timber.tag(TAG).d("submitState: change ui state = %s", state.javaClass.name)
        viewModelScope.launch() {
            _uiStateFlow.value = state
            if (state is UiState.Success<*>) initFieldStatesByUiModel((state as UiState.Success<T>).data)
        }
    }

    fun submitSingleEvent(event: E) {
        Timber.tag(TAG).d("submitSingleEvent: send single event = %s", event.javaClass.name)
        viewModelScope.launch(errorHandler) {
            _singleEventFlow.send(event)
        }
    }
}
package com.oborodulin.home.common.ui.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber

private const val TAG = "Common.MviViewModel"

abstract class MviViewModel<T : Any, S : UiState<T>, A : UiAction, E : UiSingleEvent> :
    ViewModel() {

    private val _uiStateFlow: MutableStateFlow<S> by lazy {
        MutableStateFlow(initState())
    }
    val uiStateFlow: StateFlow<S> = _uiStateFlow
    private val actionFlow: MutableSharedFlow<A> = MutableSharedFlow()
    private val _singleEventFlow = Channel<E>()
    val singleEventFlow = _singleEventFlow.receiveAsFlow()

    init {
        Timber.tag(TAG).d("init")
        viewModelScope.launch {
            Timber.tag(TAG).d("init: Start actionFlow.collect")
            actionFlow.collect {
                handleAction(it)
            }
        }
    }

    abstract fun initState(): S

    abstract fun handleAction(action: A)

    fun submitAction(action: A) {
        Timber.tag(TAG).d("submitAction: emit action = %s".format(action.javaClass.name))
        viewModelScope.launch {
            actionFlow.emit(action)
        }
    }

    fun submitState(state: S) {
        Timber.tag(TAG).d("submitState: change ui state = %s".format(state.javaClass.name))
        viewModelScope.launch {
            _uiStateFlow.value = state
        }
    }

    fun submitSingleEvent(event: E) {
        Timber.tag(TAG).d("submitSingleEvent: send single event = %s".format(event.javaClass.name))
        viewModelScope.launch {
            _singleEventFlow.send(event)
        }
    }
}
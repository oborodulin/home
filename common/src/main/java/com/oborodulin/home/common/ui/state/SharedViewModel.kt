package com.oborodulin.home.common.ui.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * https://stackoverflow.com/questions/70262850/sending-data-between-fragments-using-shared-viewmodel-and-sharedflow
 */

//@HiltViewModel
class SharedViewModel<T : Any> //@Inject constructor()
    : ViewModel() {
    private val _sharedFlow = MutableSharedFlow<T>(replay = 1)
    val sharedFlow = _sharedFlow.asSharedFlow()

    fun sendData(data: T) {
        viewModelScope.launch {
            _sharedFlow.emit(data)
        }
    }
}
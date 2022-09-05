package com.oborodulin.home.accounting.ui.payer

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oborodulin.home.accounting.domain.usecases.PayerUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

private const val TAG = "PayersListViewModel"

@HiltViewModel
class PayersListViewModel @Inject constructor(private val payerUseCases: PayerUseCases) :
    ViewModel() {
    private val _uiState = mutableStateOf(
        PayersListUiState(
            payers = listOf(),
            isLoading = true
        )
    )
    val uiState: State<PayersListUiState>
        get() = _uiState

    private val errorHandler = CoroutineExceptionHandler { _, exception ->
        Timber.tag(TAG).e(exception, exception.message)
        _uiState.value =
            _uiState.value.copy(error = exception.message, isLoading = false)
    }

    init {
        Timber.tag(TAG).d("Init payers list")
        getPayers()
    }

    private fun getPayers() {
        viewModelScope.launch(errorHandler) {
            payerUseCases.getPayers().collect {
                Timber.tag(TAG).i("Get payers for list {\"payers\": {\"count\" : ${it.size}}}")
                _uiState.value = _uiState.value.copy(
                    payers = it,
                    isLoading = false
                )
            }
        }
    }

    fun onEvent(event: PayersListEvent) {
        when (event) {
            is PayersListEvent.DeletePayer ->
                viewModelScope.launch { payerUseCases.deletePayer(event.payer) }
/*        is PayersListEvent.ShowCompletedPayers -> viewModelScope.launch {
            userPreferenceUseCases.updateShowCompleted(event.show)
        }
        is PayersListEvent.ChangeSortByDeadline -> viewModelScope.launch {
            userPreferenceUseCases.enableSortByDeadline(event.enable)
        }
        is PayersListEvent.ChangeSortByPriority -> viewModelScope.launch {
            userPreferenceUseCases.enableSortByPriority(event.enable)
        }

 */
        }
    }
}
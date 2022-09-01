package com.oborodulin.home.accounting.ui.payer.list

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oborodulin.home.accounting.domain.usecase.GetPayersUseCase
import com.oborodulin.home.accounting.ui.AccountingScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import timber.log.Timber

private const val TAG = "HomeApp.PayersListViewModel"

@HiltViewModel
class PayersListViewModel(private val getPayersUseCase: GetPayersUseCase) : ViewModel() {
    private val _uiState = mutableStateOf(
        AccountingScreenState(
            payers = listOf(),
            isLoading = true
        )
    )
    val uiState: State<AccountingScreenState>
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
            getPayersUseCase.invoke().collect {
                Timber.tag(TAG).i("Get payers for list {\"payers\": {\"count\" : ${it?.size}}}")
                _uiState.value = _uiState.value.copy(
                    payers = it,
                    isLoading = false
                )
            }
        }
    }
}
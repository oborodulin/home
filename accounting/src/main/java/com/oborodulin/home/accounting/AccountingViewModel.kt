package com.oborodulin.home.accounting

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

private const val TAG = "home.accounting.viewModel"

/**
 * Created by tfakioglu on 12.December.2021
 */
@HiltViewModel
class AccountingViewModel @Inject constructor(
    private val accountingRepository: AccountingRepository,
) : ViewModel() {

    private val _state = mutableStateOf(
        AccountingScreenState(
            payers = listOf(),
            isLoading = true
        )
    )
    val state: State<AccountingScreenState>
        get() = _state

    private val errorHandler = CoroutineExceptionHandler { _, exception ->
        exception.printStackTrace()
        _state.value = _state.value.copy(error = exception.message, isLoading = false)
    }

    //val payersList = accountingRepository.nowPlaying().cachedIn(viewModelScope)
    init {
        getPayers()
    }

    private fun getPayers() {
        viewModelScope.launch(errorHandler) {
            val payers = accountingRepository.getAll()
            _state.value = _state.value.copy(
                payers = payers,
                isLoading = false
            )
        }
    }
}

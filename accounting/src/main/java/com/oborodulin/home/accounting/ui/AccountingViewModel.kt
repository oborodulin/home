package com.oborodulin.home.accounting.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import androidx.lifecycle.ViewModel
import com.oborodulin.home.accounting.data.repositories.PayersRepositoryImp
import com.oborodulin.home.accounting.ui.payer.PayerViewState
import com.oborodulin.home.domain.entity.Payer
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import java.util.*
import javax.inject.Inject

private const val TAG = "HomeApp.accounting.viewModel"

/**
 * Created by tfakioglu on 12.December.2021
 */
@HiltViewModel
class AccountingViewModel @Inject constructor(
    private val payersRepositoryImp: PayersRepositoryImp,
) : ViewModel() {

    private val _accountingUiState = mutableStateOf(
        AccountingScreenState(
            payers = listOf(),
            isLoading = true
        )
    )
    val accountingUiState: State<AccountingScreenState>
        get() = _accountingUiState

    private val _payerUiState = mutableStateOf(
        PayerViewState(
            payer = Payer(),
            isLoading = true
        )
    )
    val payerUiState: State<PayerViewState>
        get() = _payerUiState

    private val accountingErrorHandler = CoroutineExceptionHandler { _, exception ->
        Timber.tag(TAG).e(exception, exception.message)
        _accountingUiState.value =
            _accountingUiState.value.copy(error = exception.message, isLoading = false)
    }

    private val payerErrorHandler = CoroutineExceptionHandler { _, exception ->
        Timber.tag(TAG).e(exception, exception.message)
        _payerUiState.value =
            _payerUiState.value.copy(error = exception.message, isLoading = false)
    }

    //val payersList = accountingRepository.nowPlaying().cachedIn(viewModelScope)
    init {
        Timber.tag(TAG).d("Init")
        getPayers()
    }

    private fun getPayers() {
        viewModelScope.launch(accountingErrorHandler) {
            val payers = payersRepositoryImp.getAll()
            Timber.tag(TAG).i("Get payers for list {\"payers\": {\"count\" : ${payers?.size}}}")
            _accountingUiState.value = _accountingUiState.value.copy(
                payers = payers,
                isLoading = false
            )
        }
    }

    fun getPayer(payerId: UUID) {
        viewModelScope.launch(payerErrorHandler) {
            payersRepositoryImp.get(payerId)?.let {
                Timber.tag(TAG).i("Get payer for edit {\"payer\": {\"id\" : ${it?.id}}}")
                _payerUiState.value = _payerUiState.value.copy(
                    payer = it,
                    isLoading = false
                )
            }
        }
    }

    fun savePayer(payer: Payer) {
        viewModelScope.launch(payerErrorHandler) {
            _payerUiState.value.payer?.let {
                payersRepositoryImp.update(it)
                Timber.tag(TAG).i("Save payer changes {\"payer\": {\"id\" : ${it?.id}}}")
            }
        }
    }

    fun onEvent(event: AccountingUiEvent) {
        val payer = _payerUiState.value.payer
/*        when (event) {
            is AccountingUiEvent.ErcCodeChanged -> {
            }
            is AccountingUiEvent.ConfirmAccountChanged -> {
                _uiState.value = _uiState.value.copy(
                    confirmAccountNumber = event.confirmAccount
                )
            }
            is AccountingUiEvent.CodeChanged -> {
                _uiState.value = _uiState.value.copy(
                    code = event.code
                )
            }
            is AccountingUiEvent.NameChanged -> {
                _uiState.value = _uiState.value.copy(
                    ownerName = event.name
                )
            }
            is AccountingUiEvent.Submit -> {
                validateInputs()
            }

        }
        _payerUiState.value = _payerUiState.value.copy(
            payer = payer
        )
 */
    }
}

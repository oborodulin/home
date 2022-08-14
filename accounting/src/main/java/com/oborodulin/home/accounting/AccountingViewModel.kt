package com.oborodulin.home.accounting

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import androidx.lifecycle.ViewModel
import com.oborodulin.home.accounting.payer.PayerRepository
import com.oborodulin.home.accounting.payer.PayerViewState
import com.oborodulin.home.domain.entity.Payer
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject

private const val TAG = "home.accounting.viewModel"

/**
 * Created by tfakioglu on 12.December.2021
 */
@HiltViewModel
class AccountingViewModel @Inject constructor(
    private val payerRepository: PayerRepository,
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

    private val errorHandler = CoroutineExceptionHandler { _, exception ->
        exception.printStackTrace()
        _accountingUiState.value =
            _accountingUiState.value.copy(error = exception.message, isLoading = false)
    }

    //val payersList = accountingRepository.nowPlaying().cachedIn(viewModelScope)
    init {
        getPayers()
    }

    /**
     * Устанавливает в состоянии модели список плательщиков
     * <p>Плательшики запрашиваются из репозитория</p>
     */
    private fun getPayers() {
        viewModelScope.launch(errorHandler) {
            val payers = payerRepository.getAll()
            _accountingUiState.value = _accountingUiState.value.copy(
                payers = payers,
                isLoading = false
            )
        }
    }

    /**
     * Устанавливает в состоянии модели список плательщиков
     * <p>Плательшики запрашиваются из репозитория</p>
     */
    fun getPayer(payerId: UUID) {
        viewModelScope.launch(errorHandler) {
            payerRepository.get(payerId)?.let {
                _payerUiState.value = _payerUiState.value.copy(
                    payer = it,
                    isLoading = false
                )
            }
        }
    }

    fun savePayer(payer: Payer) {
        viewModelScope.launch(errorHandler) {
            _payerUiState.value.payer?.let { payerRepository.update(it) }
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

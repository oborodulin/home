package com.oborodulin.home.accounting.ui.payer

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oborodulin.home.accounting.domain.model.Payer
import com.oborodulin.home.accounting.domain.usecases.PayerUseCases
import com.oborodulin.home.accounting.ui.AccountingScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlinx.coroutines.flow.Flow
import java.util.*

private const val TAG = "HomeApp.PayerDetailViewModel"

@HiltViewModel
class PayerDetailViewModel(private val payerUseCases: PayerUseCases) : ViewModel() {
    private val _payerState = mutableStateOf(Payer())

    private val payerState: State<Payer>
        get() = _payerState

    fun onEvent(event: PayerDetailEvent) {
        when (event) {
            is PayerDetailEvent.SavePayer -> viewModelScope.launch {
                payerUseCases.savePayer(payerState.value)
            }
            is PayerDetailEvent.ChangeErcCode -> _payerState.value =
                payerState.value.copy(ercCode = event.newErcCode)
            is PayerDetailEvent.ChangeFullName -> _payerState.value =
                payerState.value.copy(fullName = event.newFullName)
            is PayerDetailEvent.ChangeAddress -> _payerState.value =
                payerState.value.copy(address = event.newAddress)
            is PayerDetailEvent.ChangeTotalArea -> _payerState.value =
                payerState.value.copy(totalArea = event.newTotalArea)
            is PayerDetailEvent.ChangeLivingSpace -> _payerState.value =
                payerState.value.copy(livingSpace = event.newLivingSpace)
            is PayerDetailEvent.ChangeHeatedVolume -> _payerState.value =
                payerState.value.copy(heatedVolume = event.newHeatedVolume)
            is PayerDetailEvent.ChangePaymentDay -> _payerState.value =
                payerState.value.copy(paymentDay = event.newPaymentDay)
            is PayerDetailEvent.ChangePersonsNum -> _payerState.value =
                payerState.value.copy(personsNum = event.newPersonsNum)
        }
    }

    fun fetchPayer(id: UUID) {
        viewModelScope.launch() {
            payerUseCases.getPayer(id).collect {
                _payerState.value = it
            }
        }
    }
}
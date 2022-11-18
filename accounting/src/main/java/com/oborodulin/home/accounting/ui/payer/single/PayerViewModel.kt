package com.oborodulin.home.accounting.ui.payer.single

import androidx.lifecycle.viewModelScope
import com.oborodulin.home.accounting.ui.model.PayerModel
import com.oborodulin.home.accounting.ui.model.converters.PayerConverter
import com.oborodulin.home.common.ui.state.MviViewModel
import com.oborodulin.home.common.ui.state.UiSingleEvent
import com.oborodulin.home.common.ui.state.UiState
import com.oborodulin.home.domain.usecase.GetPayerUseCase
import com.oborodulin.home.domain.usecase.PayerUseCases
import com.oborodulin.home.domain.usecase.SavePayerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

private const val TAG = "Accounting.PayerViewModel"

@HiltViewModel
class PayerViewModel @Inject constructor(
    private val payerUseCases: PayerUseCases,
    private val converter: PayerConverter
) : MviViewModel<PayerModel, UiState<PayerModel>, PayerUiAction, UiSingleEvent>() {


    override fun initState(): UiState<PayerModel> = UiState.Loading

    override fun handleAction(action: PayerUiAction) {
        when (action) {
            is PayerUiAction.Load -> {
                loadPayer(action.payerId)
            }
            is PayerUiAction.ChangeErcCode -> {
                //loadPost(action.newErcCode)
            }
            is PayerUiAction.ChangeFullName -> {
                //loadPost(action.newFullName)
            }
            is PayerUiAction.ChangeAddress -> {
                //loadPost(action.newAddress)
            }
            is PayerUiAction.ChangeTotalArea -> {
                //loadPost(action.newTotalArea)
            }
            is PayerUiAction.ChangeLivingSpace -> {
                //loadPost(action.newLivingSpace)
            }
            is PayerUiAction.ChangeHeatedVolume -> {
                //loadPost(action.newHeatedVolume)
            }
            is PayerUiAction.ChangePaymentDay -> {
                //loadPost(action.newPaymentDay)
            }
            is PayerUiAction.ChangePersonsNum -> {
                //loadPost(action.newPersonsNum)
            }
            is PayerUiAction.Save -> {
                savePayer(action.payerModel)
            }
        }
    }

    private fun loadPayer(payerId: UUID) {
        viewModelScope.launch {
            payerUseCases.getPayerUseCase.execute(GetPayerUseCase.Request(payerId))
                .map {
                    converter.convert(it)
                }
                .collect {
                    submitState(it)
                }
        }
    }

    private fun savePayer(payerModel: PayerModel) {
        viewModelScope.launch {
            payerUseCases.savePayerUseCase.execute(
                SavePayerUseCase.Request(
                    converter.toPayer(payerModel)
                )
            ).collect{}
        }
    }
}

/*
        private val _payerState = mutableStateOf(Payer())

    private val payerState: State<Payer>
        get() = _payerState
    fun onEvent(event: PayerEvent) {
        when (event) {
            is PayerEvent.SavePayer -> viewModelScope.launch {
                //payerUseCases.savePayerUseCase(payerState.value)
            }
            is PayerEvent.ChangeErcCode -> _payerState.value =
                payerState.value.copy(ercCode = event.newErcCode)
            is PayerEvent.ChangeFullName -> _payerState.value =
                payerState.value.copy(fullName = event.newFullName)
            is PayerEvent.ChangeAddress -> _payerState.value =
                payerState.value.copy(address = event.newAddress)
            is PayerEvent.ChangeTotalArea -> _payerState.value =
                payerState.value.copy(totalArea = event.newTotalArea)
            is PayerEvent.ChangeLivingSpace -> _payerState.value =
                payerState.value.copy(livingSpace = event.newLivingSpace)
            is PayerEvent.ChangeHeatedVolume -> _payerState.value =
                payerState.value.copy(heatedVolume = event.newHeatedVolume)
            is PayerEvent.ChangePaymentDay -> _payerState.value =
                payerState.value.copy(paymentDay = event.newPaymentDay)
            is PayerEvent.ChangePersonsNum -> _payerState.value =
                payerState.value.copy(personsNum = event.newPersonsNum)
        }
    }
    fun fetchPayer(id: UUID) {
        viewModelScope.launch() {
            /*payerUseCases.getPayer(id).collect {
                _payerState.value = it
            }*/
        }
    }
}
 */
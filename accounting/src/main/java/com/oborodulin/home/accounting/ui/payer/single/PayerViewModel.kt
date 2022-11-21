package com.oborodulin.home.accounting.ui.payer.single

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.oborodulin.home.accounting.ui.model.PayerModel
import com.oborodulin.home.accounting.ui.model.converters.PayerConverter
import com.oborodulin.home.common.ui.components.*
import com.oborodulin.home.common.ui.components.field.InputError
import com.oborodulin.home.common.ui.components.field.InputWrapper
import com.oborodulin.home.common.ui.state.SingleViewModel
import com.oborodulin.home.common.ui.state.UiSingleEvent
import com.oborodulin.home.common.ui.state.UiState
import com.oborodulin.home.domain.usecase.GetPayerUseCase
import com.oborodulin.home.domain.usecase.PayerUseCases
import com.oborodulin.home.domain.usecase.SavePayerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

private const val TAG = "Accounting.ui.PayerViewModel"

const val ERC_CODE = "ercCode"
const val FULL_NAME = "fullName"
const val ADDRESS = "address"
const val TOTAL_AREA = "totalArea"
const val LIVING_SPACE = "livingSpace"
const val HEATED_VOLUME = "heatedVolume"
const val PAYMENT_DAY = "paymentDay"
const val PERSONS_NUM = "personsNum"
const val IS_FAVORITE = "isFavorite"

@OptIn(FlowPreview::class)
@HiltViewModel
class PayerViewModel @Inject constructor(
    private val handle: SavedStateHandle,
    private val payerUseCases: PayerUseCases,
    private val converter: PayerConverter
) : SingleViewModel<PayerModel, UiState<PayerModel>, PayerUiAction, UiSingleEvent>(
    handle,
    PayerFields.ERC_CODE
) {
    lateinit var payerModel: PayerModel

    val ercCode = handle.getStateFlow(PayerFields.ERC_CODE.name, InputWrapper())
    val fullName = handle.getStateFlow(PayerFields.FULL_NAME.name, InputWrapper())
    val areInputsValid = combine(ercCode, fullName) { ercCode, fullName ->
        ercCode.value.isNotEmpty() && ercCode.errorId == null &&
                fullName.value.isNotEmpty() && fullName.errorId == null
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    override suspend fun observeInputEvents() {
        inputEvents.receiveAsFlow()
            .onEach { event ->
                when (event) {
                    is PayerInputEvent.ErcCode -> {
                        when (PayerInputValidator.ErcCode.errorIdOrNull(event.input)) {
                            null -> {
                                handle[PayerFields.ERC_CODE.name] =
                                    ercCode.value.copy(value = event.input, errorId = null)
                            }
                            else -> {
                                handle[PayerFields.ERC_CODE.name] =
                                    ercCode.value.copy(value = event.input)
                            }
                        }
                    }
                    is PayerInputEvent.FullName -> {
                        when (PayerInputValidator.FullName.errorIdOrNull(event.input)) {
                            null -> {
                                handle[PayerFields.FULL_NAME.name] = fullName.value.copy(
                                    value = event.input,
                                    errorId = null
                                )
                            }
                            else -> {
                                handle[PayerFields.FULL_NAME.name] =
                                    fullName.value.copy(value = event.input)
                            }
                        }
                    }
                }
            }
            .debounce(350)
            .collect { event ->
                when (event) {
                    is PayerInputEvent.ErcCode -> {
                        val errorId = PayerInputValidator.ErcCode.errorIdOrNull(event.input)
                        handle[PayerFields.ERC_CODE.name] = ercCode.value.copy(errorId = errorId)
                    }
                    is PayerInputEvent.FullName -> {
                        val errorId = PayerInputValidator.FullName.errorIdOrNull(event.input)
                        handle[PayerFields.FULL_NAME.name] =
                            fullName.value.copy(errorId = errorId)
                    }
                }
            }
    }

    override fun getInputErrorsOrNull(): List<InputError>? {
        val inputErrors: MutableList<InputError> = mutableListOf()
        PayerInputValidator.ErcCode.errorIdOrNull(ercCode.value.value)?.let {
            inputErrors.add(InputError(PayerFields.ERC_CODE.name, it))
        }
        PayerInputValidator.FullName.errorIdOrNull(fullName.value.value)?.let {
            inputErrors.add(InputError(PayerFields.FULL_NAME.name, it))
        }
        return if (inputErrors.isEmpty()) null else inputErrors
    }

    override fun displayInputErrors(inputErrors: List<InputError>) {
        for (error in inputErrors) {
            handle[error.fieldName] = ercCode.value.copy(errorId = error.errorId)
        }
    }

    override fun initState(): UiState<PayerModel> = UiState.Loading

    override fun handleAction(action: PayerUiAction) {
        when (action) {
            is PayerUiAction.Load -> {
                loadPayer(action.payerId)
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
            ).collect {}
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
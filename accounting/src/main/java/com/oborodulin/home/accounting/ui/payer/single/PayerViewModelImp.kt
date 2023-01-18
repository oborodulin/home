package com.oborodulin.home.accounting.ui.payer.single

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.oborodulin.home.accounting.ui.model.PayerModel
import com.oborodulin.home.accounting.ui.model.converters.PayerConverter
import com.oborodulin.home.common.ui.components.*
import com.oborodulin.home.common.ui.components.field.*
import com.oborodulin.home.common.ui.state.SingleViewModel
import com.oborodulin.home.common.ui.state.UiSingleEvent
import com.oborodulin.home.common.ui.state.UiState
import com.oborodulin.home.domain.usecase.GetPayerUseCase
import com.oborodulin.home.domain.usecase.PayerUseCases
import com.oborodulin.home.domain.usecase.SavePayerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import javax.inject.Inject

private const val TAG = "Accounting.ui.PayerViewModel"

@OptIn(FlowPreview::class)
@HiltViewModel
class PayerViewModelImp @Inject constructor(
    private val state: SavedStateHandle,
    private val payerUseCases: PayerUseCases,
    private val converter: PayerConverter,
) : PayerViewModel, SingleViewModel<PayerModel, UiState<PayerModel>, PayerUiAction, UiSingleEvent>(
    state,
    PayerFields.ERC_CODE
) {
    private val payerId: StateFlow<InputWrapper> by lazy {
        state.getStateFlow(
            PayerFields.PAYER_ID.name,
            InputWrapper()
        )
    }
    override val ercCode: StateFlow<InputWrapper> by lazy {
        state.getStateFlow(
            PayerFields.ERC_CODE.name,
            InputWrapper()
        )
    }
    override val fullName: StateFlow<InputWrapper> by lazy {
        state.getStateFlow(
            PayerFields.FULL_NAME.name,
            InputWrapper()
        )
    }

    override val areInputsValid = combine(ercCode, fullName) { ercCode, fullName ->
        ercCode.errorId == null && fullName.errorId == null
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private val errorHandler = CoroutineExceptionHandler { _, exception ->
        Timber.tag(TAG).e(exception, exception.message)
        //_uiState.value = _uiState.value.copy(error = exception.message, isLoading = false)
    }

    override fun initState(): UiState<PayerModel> = UiState.Loading

    override suspend fun handleAction(action: PayerUiAction) {
        Timber.tag(TAG).d("handleAction(PayerUiAction) called: %s", action.javaClass.name)
        when (action) {
            is PayerUiAction.Create -> {
                initFieldStatesByUiModel(PayerModel())
            }
            is PayerUiAction.Load -> {
                loadPayer(action.payerId)
            }
            is PayerUiAction.Save -> {
                savePayer()
            }
        }
    }

    private fun loadPayer(payerId: UUID) {
        Timber.tag(TAG).d("loadPayer(UUID) called: %s", payerId.toString())
        viewModelScope.launch(errorHandler) {
            payerUseCases.getPayerUseCase.execute(GetPayerUseCase.Request(payerId))
                .map {
                    converter.convert(it)
                }
                .collect {
                    submitState(it)
                }
        }
    }

    private fun savePayer() {
        Timber.tag(TAG).d("savePayer() called")
        viewModelScope.launch(errorHandler) {
            payerUseCases.savePayerUseCase.execute(
                SavePayerUseCase.Request(
                    converter.toPayer(
                        PayerModel(
                            id = UUID.fromString(payerId.value.value),
                            ercCode = ercCode.value.value,
                            fullName = fullName.value.value
                        )
                    )
                )
            ).collect {}
        }
    }

    private fun initFieldStatesByUiModel(payerModel: PayerModel) {
        super.initFieldStatesByUiModel(payerModel)
        Timber.tag(TAG)
            .d("initFieldStatesByUiModel(PayerModel) called: payerModel = %s", payerModel)
        state[PayerFields.PAYER_ID.name] = InputWrapper(payerModel.id.toString())
        state[PayerFields.ERC_CODE.name] = InputWrapper(payerModel.ercCode)
        state[PayerFields.FULL_NAME.name] = InputWrapper(payerModel.fullName)
    }

    override suspend fun observeInputEvents() {
        Timber.tag(TAG).d("observeInputEvents() called")
        inputEvents.receiveAsFlow()
            .onEach { event ->
                when (event) {
                    is PayerInputEvent.ErcCode -> {
                        when (PayerInputValidator.ErcCode.errorIdOrNull(event.input)) {
                            null -> {
                                state[PayerFields.ERC_CODE.name] =
                                    ercCode.value.copy(value = event.input, errorId = null)
                            }
                            else -> {
                                state[PayerFields.ERC_CODE.name] =
                                    ercCode.value.copy(value = event.input)
                            }
                        }
                        Timber.tag(TAG).d("Validate: %s", ercCode.value)
                    }
                    is PayerInputEvent.FullName -> {
                        when (PayerInputValidator.FullName.errorIdOrNull(event.input)) {
                            null -> {
                                state[PayerFields.FULL_NAME.name] = fullName.value.copy(
                                    value = event.input,
                                    errorId = null
                                )
                            }
                            else -> {
                                state[PayerFields.FULL_NAME.name] =
                                    fullName.value.copy(value = event.input)
                            }
                        }
                        Timber.tag(TAG).d("Validate: %s", fullName.value)
                    }
                }
            }
            .debounce(350)
            .collect { event ->
                when (event) {
                    is PayerInputEvent.ErcCode -> {
                        val errorId = PayerInputValidator.ErcCode.errorIdOrNull(event.input)
                        state[PayerFields.ERC_CODE.name] = ercCode.value.copy(errorId = errorId)
                        Timber.tag(TAG).d(
                            "Validate (debounce): %s - %s", PayerFields.ERC_CODE.name,
                            errorId.toString()
                        )
                    }
                    is PayerInputEvent.FullName -> {
                        val errorId = PayerInputValidator.FullName.errorIdOrNull(event.input)
                        state[PayerFields.FULL_NAME.name] =
                            fullName.value.copy(errorId = errorId)
                        Timber.tag(TAG).d(
                            "Validate (debounce): %s - %s", PayerFields.FULL_NAME.name,
                            errorId.toString()
                        )
                    }
                }
            }
    }

    override fun getInputErrorsOrNull(): List<InputError>? {
        Timber.tag(TAG).d("getInputErrorsOrNull() called")
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
        Timber.tag(TAG)
            .d("displayInputErrors() called: inputErrors.count = %d", inputErrors?.size)
        for (error in inputErrors) {
            state[error.fieldName] = when (error.fieldName) {
                PayerFields.ERC_CODE.name -> ercCode.value.copy(errorId = error.errorId)
                PayerFields.FULL_NAME.name -> fullName.value.copy(errorId = error.errorId)
                else -> null
            }
        }
    }

    companion object {
        val previewModel =
            object : PayerViewModel {
                override val events = Channel<ScreenEvent>().receiveAsFlow()
                override val ercCode = MutableStateFlow(InputWrapper())
                override val fullName = MutableStateFlow(InputWrapper())
                override val areInputsValid = MutableStateFlow(true)

                override fun onTextFieldEntered(inputEvent: Inputable) {}
                override fun onTextFieldFocusChanged(focusedField: Focusable, isFocused: Boolean) {}
                override fun moveFocusImeAction() {}
                override fun onContinueClick(onSuccess: () -> Unit) {}
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
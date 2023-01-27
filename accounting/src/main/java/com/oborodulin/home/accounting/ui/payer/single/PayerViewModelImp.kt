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
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import timber.log.Timber
import java.util.*
import javax.inject.Inject

private const val TAG = "Accounting.ui.PayerViewModel"

@OptIn(FlowPreview::class)
@HiltViewModel
class PayerViewModelImp @Inject constructor(
    private val state: SavedStateHandle,
    private val payerUseCases: PayerUseCases,
    private val converter: PayerConverter
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
    override val address: StateFlow<InputWrapper> by lazy {
        state.getStateFlow(
            PayerFields.ADDRESS.name,
            InputWrapper()
        )
    }
    override val totalArea: StateFlow<InputWrapper> by lazy {
        state.getStateFlow(
            PayerFields.TOTAL_AREA.name,
            InputWrapper()
        )
    }
    override val livingSpace: StateFlow<InputWrapper> by lazy {
        state.getStateFlow(
            PayerFields.LIVING_SPACE.name,
            InputWrapper()
        )
    }
    override val heatedVolume: StateFlow<InputWrapper> by lazy {
        state.getStateFlow(
            PayerFields.HEATED_VOLUME.name,
            InputWrapper()
        )
    }
    override val paymentDay: StateFlow<InputWrapper> by lazy {
        state.getStateFlow(
            PayerFields.PAYMENT_DAY.name,
            InputWrapper()
        )
    }
    override val personsNum: StateFlow<InputWrapper> by lazy {
        state.getStateFlow(
            PayerFields.PERSONS_NUM.name,
            InputWrapper()
        )
    }

    override val areInputsValid =
        combine(
            combine(
                ercCode,
                fullName,
                address,
                totalArea
            ) { ercCode, fullName, address, totalArea ->
                ercCode.errorId == null && fullName.errorId == null && address.errorId == null && totalArea.errorId == null
            },
            combine(
                livingSpace,
                heatedVolume,
                paymentDay,
                personsNum
            ) { livingSpace, heatedVolume, paymentDay, personsNum ->
                livingSpace.errorId == null && heatedVolume.errorId == null && paymentDay.errorId == null && personsNum.errorId == null
            }) { fieldsPartOne, fieldsPartTwo -> fieldsPartOne && fieldsPartTwo }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            false
        )

    private val errorHandler = CoroutineExceptionHandler { _, exception ->
        Timber.tag(TAG).e(exception)
        //_uiState.value = _uiState.value.copy(error = exception.message, isLoading = false)
    }

    override fun initState(): UiState<PayerModel> = UiState.Loading

    override suspend fun handleAction(action: PayerUiAction): Job {
        Timber.tag(TAG).d("handleAction(PayerUiAction) called: %s", action.javaClass.name)
        val job = when (action) {
            is PayerUiAction.Create -> {
                submitState(UiState.Success(PayerModel()))
            }
            is PayerUiAction.Load -> {
                loadPayer(action.payerId)
            }
            is PayerUiAction.Save -> {
                savePayer()
            }
        }
        return job
    }

    private fun loadPayer(payerId: UUID): Job {
        Timber.tag(TAG).d("loadPayer(UUID) called: %s", payerId.toString())
        val job = viewModelScope.launch(errorHandler) {
            payerUseCases.getPayerUseCase.execute(GetPayerUseCase.Request(payerId))
                .map {
                    converter.convert(it)
                }
                .collect {
                    submitState(it)
                }
        }
        return job
    }

    private fun savePayer(): Job {
        Timber.tag(TAG).d("savePayer() called")
        val job = viewModelScope.launch(errorHandler) {
            payerUseCases.savePayerUseCase.execute(
                SavePayerUseCase.Request(
                    converter.toPayer(
                        PayerModel(
                            id = UUID.fromString(payerId.value.value),
                            ercCode = ercCode.value.value,
                            fullName = fullName.value.value,
                            address = address.value.value,
                            totalArea = totalArea.value.value.toBigDecimalOrNull(),
                            livingSpace = livingSpace.value.value.toBigDecimalOrNull(),
                            heatedVolume = heatedVolume.value.value.toBigDecimalOrNull(),
                            paymentDay = paymentDay.value.value.toIntOrNull(),
                            personsNum = personsNum.value.value.toIntOrNull()
                        )
                    )
                )
            ).collect {}
        }
        return job
    }

    override fun initFieldStatesByUiModel(uiModel: Any): Job? {
        super.initFieldStatesByUiModel(uiModel)
        val payerModel = uiModel as PayerModel
        Timber.tag(TAG)
            .d("initFieldStatesByUiModel(PayerModel) called: payerModel = %s", payerModel)
        state[PayerFields.PAYER_ID.name] = payerId.value.copy(value = payerModel.id.toString())
        state[PayerFields.ERC_CODE.name] = ercCode.value.copy(value = payerModel.ercCode)
        state[PayerFields.FULL_NAME.name] = fullName.value.copy(value = payerModel.fullName)
        state[PayerFields.ADDRESS.name] = address.value.copy(value = payerModel.address)

        val strTotalArea = payerModel.totalArea?.toString() ?: ""
        state[PayerFields.TOTAL_AREA.name] = totalArea.value.copy(value = strTotalArea)

        val strlivingSpace = payerModel.livingSpace?.toString() ?: ""
        state[PayerFields.LIVING_SPACE.name] = livingSpace.value.copy(value = strlivingSpace)

        state[PayerFields.HEATED_VOLUME.name] =
            heatedVolume.value.copy(value = payerModel.heatedVolume?.toString() ?: "")
        state[PayerFields.PAYMENT_DAY.name] =
            paymentDay.value.copy(value = payerModel.paymentDay?.toString() ?: "")
        state[PayerFields.PERSONS_NUM.name] =
            personsNum.value.copy(value = payerModel.personsNum?.toString() ?: "")
        return null
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
                        Timber.tag(TAG)
                            .d("Validate: %s = %s", PayerFields.ERC_CODE.name, ercCode.value)
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
                        Timber.tag(TAG)
                            .d("Validate: %s = %s", PayerFields.FULL_NAME.name, fullName.value)
                    }
                    is PayerInputEvent.Address -> {
                        when (PayerInputValidator.Address.errorIdOrNull(event.input)) {
                            null -> {
                                state[PayerFields.ADDRESS.name] = address.value.copy(
                                    value = event.input,
                                    errorId = null
                                )
                            }
                            else -> {
                                state[PayerFields.ADDRESS.name] =
                                    address.value.copy(value = event.input)
                            }
                        }
                        Timber.tag(TAG)
                            .d("Validate: %s = %s", PayerFields.ADDRESS.name, address.value)
                    }
                    is PayerInputEvent.TotalArea -> {
                        when (PayerInputValidator.TotalArea.errorIdOrNull(event.input)) {
                            null -> {
                                state[PayerFields.TOTAL_AREA.name] = totalArea.value.copy(
                                    value = event.input,
                                    errorId = null
                                )
                            }
                            else -> {
                                state[PayerFields.TOTAL_AREA.name] =
                                    totalArea.value.copy(value = event.input)
                            }
                        }
                        Timber.tag(TAG)
                            .d("Validate: %s = %s", PayerFields.TOTAL_AREA.name, totalArea.value)
                    }
                    is PayerInputEvent.LivingSpace -> {
                        when (PayerInputValidator.LivingSpace.errorIdOrNull(event.input)) {
                            null -> {
                                state[PayerFields.LIVING_SPACE.name] = livingSpace.value.copy(
                                    value = event.input,
                                    errorId = null
                                )
                            }
                            else -> {
                                state[PayerFields.LIVING_SPACE.name] =
                                    livingSpace.value.copy(value = event.input)
                            }
                        }
                        Timber.tag(TAG).d(
                            "Validate: %s = %s",
                            PayerFields.LIVING_SPACE.name,
                            livingSpace.value
                        )
                    }
                    is PayerInputEvent.HeatedVolume -> {
                        when (PayerInputValidator.HeatedVolume.errorIdOrNull(event.input)) {
                            null -> {
                                state[PayerFields.HEATED_VOLUME.name] = heatedVolume.value.copy(
                                    value = event.input,
                                    errorId = null
                                )
                            }
                            else -> {
                                state[PayerFields.HEATED_VOLUME.name] =
                                    heatedVolume.value.copy(value = event.input)
                            }
                        }
                        Timber.tag(TAG).d(
                            "Validate: %s = %s",
                            PayerFields.HEATED_VOLUME.name,
                            heatedVolume.value
                        )
                    }
                    is PayerInputEvent.PaymentDay -> {
                        when (PayerInputValidator.PaymentDay.errorIdOrNull(event.input)) {
                            null -> {
                                state[PayerFields.PAYMENT_DAY.name] = paymentDay.value.copy(
                                    value = event.input,
                                    errorId = null
                                )
                            }
                            else -> {
                                state[PayerFields.PAYMENT_DAY.name] =
                                    paymentDay.value.copy(value = event.input)
                            }
                        }
                        Timber.tag(TAG).d(
                            "Validate: %s = %s",
                            PayerFields.PAYMENT_DAY.name,
                            paymentDay.value
                        )
                    }
                    is PayerInputEvent.PersonsNum -> {
                        when (PayerInputValidator.PersonsNum.errorIdOrNull(event.input)) {
                            null -> {
                                state[PayerFields.PERSONS_NUM.name] = personsNum.value.copy(
                                    value = event.input,
                                    errorId = null
                                )
                            }
                            else -> {
                                state[PayerFields.PERSONS_NUM.name] =
                                    personsNum.value.copy(value = event.input)
                            }
                        }
                        Timber.tag(TAG).d(
                            "Validate: %s = %s",
                            PayerFields.PERSONS_NUM.name,
                            personsNum.value
                        )
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
                            "Validate (debounce): %s - ERR[%s]", PayerFields.ERC_CODE.name,
                            errorId?.toString()
                        )
                    }
                    is PayerInputEvent.FullName -> {
                        val errorId = PayerInputValidator.FullName.errorIdOrNull(event.input)
                        state[PayerFields.FULL_NAME.name] =
                            fullName.value.copy(errorId = errorId)
                        Timber.tag(TAG).d(
                            "Validate (debounce): %s - ERR[%s]", PayerFields.FULL_NAME.name,
                            errorId?.toString()
                        )
                    }
                    is PayerInputEvent.Address -> {
                        val errorId = PayerInputValidator.Address.errorIdOrNull(event.input)
                        state[PayerFields.ADDRESS.name] =
                            address.value.copy(errorId = errorId)
                        Timber.tag(TAG).d(
                            "Validate (debounce): %s - ERR[%s]", PayerFields.ADDRESS.name,
                            errorId?.toString()
                        )
                    }
                    is PayerInputEvent.TotalArea -> {
                        val errorId = PayerInputValidator.TotalArea.errorIdOrNull(event.input)
                        state[PayerFields.TOTAL_AREA.name] =
                            totalArea.value.copy(errorId = errorId)
                        Timber.tag(TAG).d(
                            "Validate (debounce): %s - ERR[%s]", PayerFields.TOTAL_AREA.name,
                            errorId?.toString()
                        )
                    }
                    is PayerInputEvent.LivingSpace -> {
                        val errorId = PayerInputValidator.LivingSpace.errorIdOrNull(event.input)
                        state[PayerFields.LIVING_SPACE.name] =
                            livingSpace.value.copy(errorId = errorId)
                        Timber.tag(TAG).d(
                            "Validate (debounce): %s - ERR[%s]", PayerFields.LIVING_SPACE.name,
                            errorId?.toString()
                        )
                    }
                    is PayerInputEvent.HeatedVolume -> {
                        val errorId = PayerInputValidator.HeatedVolume.errorIdOrNull(event.input)
                        state[PayerFields.HEATED_VOLUME.name] =
                            heatedVolume.value.copy(errorId = errorId)
                        Timber.tag(TAG).d(
                            "Validate (debounce): %s - ERR[%s]", PayerFields.HEATED_VOLUME.name,
                            errorId?.toString()
                        )
                    }
                    is PayerInputEvent.PaymentDay -> {
                        val errorId = PayerInputValidator.PaymentDay.errorIdOrNull(event.input)
                        state[PayerFields.PAYMENT_DAY.name] =
                            paymentDay.value.copy(errorId = errorId)
                        Timber.tag(TAG).d(
                            "Validate (debounce): %s - ERR[%s]", PayerFields.PAYMENT_DAY.name,
                            errorId?.toString()
                        )
                    }
                    is PayerInputEvent.PersonsNum -> {
                        val errorId = PayerInputValidator.PersonsNum.errorIdOrNull(event.input)
                        state[PayerFields.PERSONS_NUM.name] =
                            personsNum.value.copy(errorId = errorId)
                        Timber.tag(TAG).d(
                            "Validate (debounce): %s - ERR[%s]", PayerFields.PERSONS_NUM.name,
                            errorId?.toString()
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
        PayerInputValidator.Address.errorIdOrNull(address.value.value)?.let {
            inputErrors.add(InputError(PayerFields.ADDRESS.name, it))
        }
        PayerInputValidator.TotalArea.errorIdOrNull(totalArea.value.value)?.let {
            inputErrors.add(InputError(PayerFields.TOTAL_AREA.name, it))
        }
        PayerInputValidator.LivingSpace.errorIdOrNull(livingSpace.value.value)?.let {
            inputErrors.add(InputError(PayerFields.LIVING_SPACE.name, it))
        }
        PayerInputValidator.HeatedVolume.errorIdOrNull(heatedVolume.value.value)?.let {
            inputErrors.add(InputError(PayerFields.HEATED_VOLUME.name, it))
        }
        PayerInputValidator.PaymentDay.errorIdOrNull(paymentDay.value.value)?.let {
            inputErrors.add(InputError(PayerFields.PAYMENT_DAY.name, it))
        }
        PayerInputValidator.PersonsNum.errorIdOrNull(personsNum.value.value)?.let {
            inputErrors.add(InputError(PayerFields.PERSONS_NUM.name, it))
        }
        return if (inputErrors.isEmpty()) null else inputErrors
    }

    override fun displayInputErrors(inputErrors: List<InputError>) {
        Timber.tag(TAG)
            .d("displayInputErrors() called: inputErrors.count = %d", inputErrors.size)
        for (error in inputErrors) {
            state[error.fieldName] = when (error.fieldName) {
                PayerFields.ERC_CODE.name -> ercCode.value.copy(errorId = error.errorId)
                PayerFields.FULL_NAME.name -> fullName.value.copy(errorId = error.errorId)
                PayerFields.ADDRESS.name -> address.value.copy(errorId = error.errorId)
                PayerFields.TOTAL_AREA.name -> totalArea.value.copy(errorId = error.errorId)
                PayerFields.LIVING_SPACE.name -> livingSpace.value.copy(errorId = error.errorId)
                PayerFields.HEATED_VOLUME.name -> heatedVolume.value.copy(errorId = error.errorId)
                PayerFields.PAYMENT_DAY.name -> paymentDay.value.copy(errorId = error.errorId)
                PayerFields.PERSONS_NUM.name -> personsNum.value.copy(errorId = error.errorId)
                else -> null
            }
        }
    }

    companion object {
        val previewModel =
            object : PayerViewModel {
                override val events = Channel<ScreenEvent>().receiveAsFlow()
                override val actionsJobFlow: SharedFlow<Job?> = MutableSharedFlow()

                override val ercCode = MutableStateFlow(InputWrapper())
                override val fullName = MutableStateFlow(InputWrapper())
                override val address = MutableStateFlow(InputWrapper())
                override val totalArea = MutableStateFlow(InputWrapper())
                override val livingSpace = MutableStateFlow(InputWrapper())
                override val heatedVolume = MutableStateFlow(InputWrapper())
                override val paymentDay = MutableStateFlow(InputWrapper())
                override val personsNum = MutableStateFlow(InputWrapper())

                override val areInputsValid = MutableStateFlow(true)

                override fun viewModelScope(): CoroutineScope = CoroutineScope(Dispatchers.Main)
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
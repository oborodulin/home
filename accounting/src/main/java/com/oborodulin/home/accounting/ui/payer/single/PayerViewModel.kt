package com.oborodulin.home.accounting.ui.payer.single

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.oborodulin.home.accounting.ui.model.PayerModel
import com.oborodulin.home.accounting.ui.model.converters.PayerConverter
import com.oborodulin.home.common.ui.components.InputErrors
import com.oborodulin.home.common.ui.components.InputValidator
import com.oborodulin.home.common.ui.components.InputWrapper
import com.oborodulin.home.common.ui.components.ScreenEvent
import com.oborodulin.home.common.ui.state.MviViewModel
import com.oborodulin.home.common.ui.state.UiSingleEvent
import com.oborodulin.home.common.ui.state.UiState
import com.oborodulin.home.domain.usecase.GetPayerUseCase
import com.oborodulin.home.domain.usecase.PayerUseCases
import com.oborodulin.home.domain.usecase.SavePayerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
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
) : MviViewModel<PayerModel, UiState<PayerModel>, PayerUiAction, UiSingleEvent>() {
    lateinit var payerModel: PayerModel

    val ercCode = handle.getStateFlow(ERC_CODE, InputWrapper())
    val fullName = handle.getStateFlow(FULL_NAME, InputWrapper())
    val areInputsValid = combine(ercCode, fullName) { ercCode, fullName ->
        ercCode.value.isNotEmpty() && ercCode.errorId == null &&
                fullName.value.isNotEmpty() && fullName.errorId == null
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    private var focusedTextField = handle["focusedTextField"] ?: PayerFocusedTextFieldKey.ERC_CODE
        set(value) {
            field = value
            handle["focusedTextField"] = value
        }

    private val _events = Channel<ScreenEvent>()
    val events = _events.receiveAsFlow()
    private val inputEvents = Channel<PayerInputEvent>(Channel.CONFLATED)

    init {
        observePayerInputEvents()
        if (focusedTextField != PayerFocusedTextFieldKey.NONE) focusOnLastSelectedTextField()
    }

    private fun observePayerInputEvents() {
        viewModelScope.launch(Dispatchers.Default) {
            inputEvents.receiveAsFlow()
                .onEach { event ->
                    when (event) {
                        is PayerInputEvent.ErcCode -> {
                            when (InputValidator.getNameErrorIdOrNull(event.input)) {
                                null -> {
                                    handle[ERC_CODE] =
                                        ercCode.value.copy(value = event.input, errorId = null)
                                }
                                else -> {
                                    handle[ERC_CODE] = ercCode.value.copy(value = event.input)
                                }
                            }
                        }
                        is PayerInputEvent.FullName -> {
                            when (InputValidator.getCardNumberErrorIdOrNull(event.input)) {
                                null -> {
                                    handle[FULL_NAME] = fullName.value.copy(
                                        value = event.input,
                                        errorId = null
                                    )
                                }
                                else -> {
                                    handle[FULL_NAME] =
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
                            val errorId = InputValidator.getNameErrorIdOrNull(event.input)
                            handle[ERC_CODE] = ercCode.value.copy(errorId = errorId)
                        }
                        is PayerInputEvent.FullName -> {
                            val errorId = InputValidator.getCardNumberErrorIdOrNull(event.input)
                            handle[FULL_NAME] =
                                fullName.value.copy(errorId = errorId)
                        }
                    }
                }
        }
    }

    fun onNameEntered(input: String) {
        inputEvents.trySend(PayerInputEvent.ErcCode(input))
    }

    fun onCardNumberEntered(input: String) {
        inputEvents.trySend(PayerInputEvent.FullName(input))
    }

    fun onTextFieldFocusChanged(key: PayerFocusedTextFieldKey, isFocused: Boolean) {
        focusedTextField = if (isFocused) key else PayerFocusedTextFieldKey.NONE
    }

    fun onNameImeActionClick() {
        _events.trySend(ScreenEvent.MoveFocus())
    }

    fun onContinueClick() {
        viewModelScope.launch(Dispatchers.Default) {
            when (val inputErrors = getInputErrorsOrNull()) {
                null -> {
                    clearFocusAndHideKeyboard()
                    _events.send(ScreenEvent.ShowToast(com.oborodulin.home.common.R.string.success))
                }
                else -> displayInputErrors(inputErrors)
            }
        }
    }

    private fun getInputErrorsOrNull(): InputErrors? {
        val nameErrorId = InputValidator.getNameErrorIdOrNull(ercCode.value.value)
        val cardErrorId = InputValidator.getCardNumberErrorIdOrNull(fullName.value.value)
        return if (nameErrorId == null && cardErrorId == null) {
            null
        } else {
            InputErrors(nameErrorId, cardErrorId)
        }
    }

    private fun displayInputErrors(inputErrors: InputErrors) {
        handle[ERC_CODE] = ercCode.value.copy(errorId = inputErrors.nameErrorId)
        handle[FULL_NAME] = fullName.value.copy(errorId = inputErrors.cardErrorId)
    }

    private suspend fun clearFocusAndHideKeyboard() {
        _events.send(ScreenEvent.ClearFocus)
        _events.send(ScreenEvent.UpdateKeyboard(false))
        focusedTextField = PayerFocusedTextFieldKey.NONE
    }

    private fun focusOnLastSelectedTextField() {
        viewModelScope.launch(Dispatchers.Default) {
            _events.send(ScreenEvent.RequestFocus(focusedTextField))
            delay(250)
            _events.send(ScreenEvent.UpdateKeyboard(true))
        }
    }

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
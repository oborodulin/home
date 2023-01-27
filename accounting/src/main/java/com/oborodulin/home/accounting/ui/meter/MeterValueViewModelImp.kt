package com.oborodulin.home.accounting.ui.meter

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.oborodulin.home.common.ui.components.*
import com.oborodulin.home.common.ui.components.field.*
import com.oborodulin.home.common.ui.state.SingleViewModel
import com.oborodulin.home.common.ui.state.UiSingleEvent
import com.oborodulin.home.common.ui.state.UiState
import com.oborodulin.home.metering.domain.usecases.MeterUseCases
import com.oborodulin.home.metering.domain.usecases.SaveMeterValueUseCase
import com.oborodulin.home.metering.ui.model.MeterValueModel
import com.oborodulin.home.metering.ui.model.converters.MeterValueConverter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import javax.inject.Inject

private const val TAG = "Accounting.ui.MeterValueViewModel"

@OptIn(FlowPreview::class)
@HiltViewModel
class MeterValueViewModelImp @Inject constructor(
    private val state: SavedStateHandle,
    private val meterUseCases: MeterUseCases,
    private val converter: MeterValueConverter,
) : MeterValueViewModel,
    SingleViewModel<MeterValueModel, UiState<MeterValueModel>, MeterValueUiAction, UiSingleEvent>(
        state
    ) {
    private val meterValueId: StateFlow<InputWrapper> by lazy {
        state.getStateFlow(
            MeterValueFields.METER_VALUE_ID.name,
            InputWrapper()
        )
    }
    private val metersId: StateFlow<InputWrapper> by lazy {
        state.getStateFlow(
            MeterValueFields.METERS_ID.name,
            InputWrapper()
        )
    }
    override val currentValue: StateFlow<InputWrapper> by lazy {
        state.getStateFlow(
            MeterValueFields.METER_CURR_VALUE.name,
            InputWrapper()
        )
    }

    override val areInputsValid = combine(currentValue) { currentValue ->
        currentValue[0].errorId == null
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private val errorHandler = CoroutineExceptionHandler { _, exception ->
        Timber.tag(TAG).e(exception)
        //_uiState.value = _uiState.value.copy(error = exception.message, isLoading = false)
    }

    override fun initState() = UiState.Loading

    override suspend fun handleAction(action: MeterValueUiAction): Job {
        Timber.tag(TAG)
            .d("handleAction(MeterValueUiAction) called: %s", action.javaClass.name)
        val job = when (action) {
            is MeterValueUiAction.Save -> {
                saveMeterValue()
            }
        }
        return job
    }

    private fun saveMeterValue(): Job {
        Timber.tag(TAG).d("saveMeterValue() called")
        val job = viewModelScope.launch(errorHandler) {
            meterUseCases.saveMeterValueUseCase.execute(
                SaveMeterValueUseCase.Request(
                    converter.toMeterValue(
                        MeterValueModel(
                            id = UUID.fromString(meterValueId.value.value),
                            metersId = UUID.fromString(metersId.value.value),
                            currentValue = currentValue.value.value.toBigDecimal(),
                        )
                    )
                )
            ).collect {}
        }
        return job
    }

    override fun initFieldStatesByUiModel(uiModel: Any): Job? {
        super.initFieldStatesByUiModel(uiModel)
        val meterValueModel = uiModel as MeterValueModel
        Timber.tag(TAG)
            .d(
                "initFieldStatesByUiModel(MeterValueModel) called: meterValueModel = %s",
                meterValueModel
            )
        state[MeterValueFields.METER_VALUE_ID.name] =
            meterValueId.value.copy(value = meterValueModel.id.toString())
        state[MeterValueFields.METERS_ID.name] =
            meterValueId.value.copy(value = meterValueModel.metersId.toString())
        meterValueModel.currentValue?.let {
            state[MeterValueFields.METER_CURR_VALUE.name] =
                currentValue.value.copy(value = it.toString())
        }
        return null
    }

    override suspend fun observeInputEvents() {
        Timber.tag(TAG).d("observeInputEvents() called")
        inputEvents.receiveAsFlow()
            .onEach { event ->
                when (event) {
                    is MeterValueInputEvent.CurrentValue -> {
                        when (MeterValueInputValidator.CurrentValue.errorIdOrNull(event.input)) {
                            null -> {
                                state[MeterValueFields.METER_CURR_VALUE.name] =
                                    currentValue.value.copy(value = event.input, errorId = null)
                            }
                            else -> {
                                state[MeterValueFields.METER_CURR_VALUE.name] =
                                    currentValue.value.copy(value = event.input)
                            }
                        }
                        //Timber.tag(TAG)
                        //    .d("Validate: %s".format(state[MeterValueFields.METER_CURR_VALUE.name]))
                    }
                }
            }
            .debounce(350)
            .collect { event ->
                when (event) {
                    is MeterValueInputEvent.CurrentValue -> {
                        val errorId =
                            MeterValueInputValidator.CurrentValue.errorIdOrNull(event.input)
                        state[MeterValueFields.METER_CURR_VALUE.name] =
                            currentValue.value.copy(errorId = errorId)
                        Timber.tag(TAG).d(
                            "Validate (debounce): %s - %s",
                            MeterValueFields.METER_CURR_VALUE.name,
                            errorId
                        )
                    }
                }
            }
    }

    override fun getInputErrorsOrNull(): List<InputError>? {
        Timber.tag(TAG).d("getInputErrorsOrNull() called")
        val inputErrors: MutableList<InputError> = mutableListOf()
        MeterValueInputValidator.CurrentValue.errorIdOrNull(currentValue.value.value)?.let {
            inputErrors.add(InputError(MeterValueFields.METER_CURR_VALUE.name, it))
        }
        return if (inputErrors.isEmpty()) null else inputErrors
    }

    override fun displayInputErrors(inputErrors: List<InputError>) {
        Timber.tag(TAG)
            .d("displayInputErrors() called: inputErrors.count = %d", inputErrors.size)
        for (error in inputErrors) {
            state[error.fieldName] = when (error.fieldName) {
                MeterValueFields.METER_CURR_VALUE.name -> currentValue.value.copy(errorId = error.errorId)
                else -> null
            }
        }
    }

    companion object {
        val previewModel =
            object : MeterValueViewModel {
                override val events = Channel<ScreenEvent>().receiveAsFlow()

                override val currentValue = MutableStateFlow(InputWrapper())
                override val areInputsValid = MutableStateFlow(true)

                override fun initFieldStatesByUiModel(uiModel: Any): Job? = null
                override fun onTextFieldEntered(inputEvent: Inputable) {}
                override fun onTextFieldFocusChanged(focusedField: Focusable, isFocused: Boolean) {}
                override fun onContinueClick(onSuccess: () -> Unit) {}
                override fun submitAction(action: MeterValueUiAction): Job? = null
            }
    }
}
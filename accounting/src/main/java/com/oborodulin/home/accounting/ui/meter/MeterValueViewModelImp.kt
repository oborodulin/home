package com.oborodulin.home.accounting.ui.meter

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.oborodulin.home.common.ui.components.*
import com.oborodulin.home.common.ui.components.field.*
import com.oborodulin.home.common.ui.components.field.util.*
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
    SingleViewModel<MeterValueModel, UiState<MeterValueModel>, MeterValueUiAction, UiSingleEvent, MeterValueFields>(
        state
    ) {
    private val meterValueId: StateFlow<InputWrapper> by lazy {
        state.getStateFlow(
            stateKey(MeterValueFields.METER_VALUE_ID), InputWrapper()
        )
    }
    private val metersId: StateFlow<InputWrapper> by lazy {
        state.getStateFlow(
            stateKey(MeterValueFields.METERS_ID), InputWrapper()
        )
    }
    override val currentValue: StateFlow<InputWrapper> by lazy {
        state.getStateFlow(
            stateKey(MeterValueFields.METER_CURR_VALUE), InputWrapper()
        )
    }

    override val areInputsValid = combine(currentValue) { currentValue ->
        currentValue[0].errorId == null
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private val errorHandler = CoroutineExceptionHandler { _, exception ->
        Timber.tag(TAG).e(exception)
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

    override fun stateInputFields() = enumValues<MeterValueFields>().map { it.name }

    override fun initFieldStatesByUiModel(uiModel: Any): Job? {
        super.initFieldStatesByUiModel(uiModel)
        val meterValueModel = uiModel as MeterValueModel
        Timber.tag(TAG)
            .d(
                "initFieldStatesByUiModel(MeterValueModel) called: meterValueModel = %s",
                meterValueModel
            )
        meterValueModel.id?.let {
            initStateValue(MeterValueFields.METER_VALUE_ID, meterValueId, it.toString())
        }
        initStateValue(
            MeterValueFields.METERS_ID, metersId, meterValueModel.metersId.toString()
        )
        meterValueModel.currentValue?.let {
            initStateValue(MeterValueFields.METER_CURR_VALUE, currentValue, it.toString())
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
                            null -> setStateValidValue(
                                MeterValueFields.METER_CURR_VALUE, currentValue, event.input
                            )
                            else -> setStateValue(
                                MeterValueFields.METER_CURR_VALUE, currentValue, event.input
                            )
                        }
                    }
                }
            }
            .debounce(350)
            .collect { event ->
                when (event) {
                    is MeterValueInputEvent.CurrentValue ->
                        setStateValue(
                            MeterValueFields.METER_CURR_VALUE, currentValue,
                            MeterValueInputValidator.CurrentValue.errorIdOrNull(event.input)
                        )
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
                override fun onTextFieldFocusChanged(
                    focusedField: MeterValueFields,
                    isFocused: Boolean
                ) {
                }

                override fun onContinueClick(onSuccess: () -> Unit) {}
                override fun submitAction(action: MeterValueUiAction): Job? = null
            }
    }
}
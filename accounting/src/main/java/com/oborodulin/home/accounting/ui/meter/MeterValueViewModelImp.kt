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
    private val meterValueId: StateFlow<InputsWrapper> by lazy {
        state.getStateFlow(MeterValueFields.METER_VALUE_ID.name, InputsWrapper())
    }
    private val metersId: StateFlow<InputsWrapper> by lazy {
        state.getStateFlow(MeterValueFields.METERS_ID.name, InputsWrapper())
    }
    override val currentValue: StateFlow<InputsWrapper> by lazy {
        state.getStateFlow(MeterValueFields.METER_CURR_VALUE.name, InputsWrapper())
    }

    override val areInputsValid = combine(currentValue) { currentValue ->
        currentValue[0].inputs.filter { it.value.errorId == null }.size == currentValue[0].inputs.size
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
            // unsaved values
            currentValue.value.inputs.filter { entry -> !entry.value.isSaved }
                .forEach { (key, curVal) ->
                    meterUseCases.saveMeterValueUseCase.execute(
                        SaveMeterValueUseCase.Request(
                            converter.toMeterValue(
                                MeterValueModel(
                                    id = UUID.fromString(meterValueId.value.inputs[key]?.value),
                                    metersId = UUID.fromString(key),
                                    currentValue = curVal.value.toBigDecimalOrNull(),
                                )
                            )
                        )
                    ).collect {}
                }
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
            initStateValue(
                field = MeterValueFields.METER_VALUE_ID, properties = meterValueId,
                value = it.toString(), key = meterValueModel.metersId.toString()
            )
        }
        initStateValue(
            field = MeterValueFields.METERS_ID, properties = metersId,
            value = meterValueModel.metersId.toString(), key = meterValueModel.metersId.toString()
        )
        initStateValue(
            field = MeterValueFields.METER_CURR_VALUE, properties = currentValue,
            value = meterValueModel.currentValue?.toString() ?: "",
            key = meterValueModel.metersId.toString()
        )
        return null
    }

    override suspend fun observeInputEvents() {
        Timber.tag(TAG).d("observeInputEvents() called")
        inputEvents.receiveAsFlow()
            .onEach { event ->
                when (event) {
                    is MeterValueInputEvent.CurrentValue -> {
                        when (MeterValueInputValidator.CurrentValue.errorIdOrNull(event.input)) {
                            null -> setStateValue(
                                field = MeterValueFields.METER_CURR_VALUE,
                                properties = currentValue,
                                value = event.input,
                                key = event.key,
                                isValid = true
                            )
                            else -> setStateValue(
                                field = MeterValueFields.METER_CURR_VALUE,
                                properties = currentValue,
                                value = event.input,
                                key = event.key
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
                            field = MeterValueFields.METER_CURR_VALUE, properties = currentValue,
                            key = event.key,
                            errorId = MeterValueInputValidator.CurrentValue.errorIdOrNull(event.input)
                        )
                }
            }
    }

    override fun getInputErrorsOrNull(): List<InputError>? {
        Timber.tag(TAG).d("getInputErrorsOrNull() called")
        val inputErrors: MutableList<InputError> = mutableListOf()
        currentValue.value.inputs.forEach { entry ->
            MeterValueInputValidator.CurrentValue.errorIdOrNull(entry.value.value)?.let { errorId ->
                inputErrors.add(
                    InputError(
                        fieldName = MeterValueFields.METER_CURR_VALUE.name,
                        key = entry.key,
                        errorId = errorId
                    )
                )
            }
        }
        return if (inputErrors.isEmpty()) null else inputErrors
    }

    override fun displayInputErrors(inputErrors: List<InputError>) {
        Timber.tag(TAG)
            .d("displayInputErrors() called: inputErrors.count = %d", inputErrors.size)
        for (error in inputErrors) {
            state[error.fieldName] = when (error.fieldName) {
                MeterValueFields.METER_CURR_VALUE.name ->
                    currentValue.value.inputs[error.key]?.copy(errorId = error.errorId)
                else -> null
            }
        }
    }

    companion object {
        val previewModel =
            object : MeterValueViewModel {
                override val events = Channel<ScreenEvent>().receiveAsFlow()

                override val currentValue = MutableStateFlow(InputsWrapper())
                override val areInputsValid = MutableStateFlow(true)

                override fun initFieldStatesByUiModel(uiModel: Any): Job? = null
                override fun onTextFieldEntered(inputEvent: Inputable) {}
                override fun onTextFieldFocusChanged(
                    focusedField: MeterValueFields, isFocused: Boolean
                ) {
                }

                override fun onContinueClick(onSuccess: () -> Unit) {}
                override fun submitAction(action: MeterValueUiAction): Job? = null
            }
    }
}
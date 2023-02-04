package com.oborodulin.home.metering.ui.value

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.oborodulin.home.common.domain.entities.Result
import com.oborodulin.home.common.ui.components.*
import com.oborodulin.home.common.ui.components.field.*
import com.oborodulin.home.common.ui.components.field.util.*
import com.oborodulin.home.common.ui.state.SingleViewModel
import com.oborodulin.home.common.ui.state.UiState
import com.oborodulin.home.common.util.Utils
import com.oborodulin.home.data.R
import com.oborodulin.home.data.util.ServiceType
import com.oborodulin.home.metering.domain.usecases.GetPrevServiceMeterValuesUseCase
import com.oborodulin.home.metering.domain.usecases.MeterUseCases
import com.oborodulin.home.metering.domain.usecases.SaveMeterValueUseCase
import com.oborodulin.home.metering.ui.model.MeterValueListItemModel
import com.oborodulin.home.metering.ui.model.converters.MeterValueConverter
import com.oborodulin.home.metering.ui.model.converters.PrevServiceMeterValuesListConverter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import java.math.BigDecimal
import java.text.DecimalFormat
import java.time.OffsetDateTime
import java.util.*
import javax.inject.Inject

private const val TAG = "Metering.ui.MeterValueViewModel"

@OptIn(FlowPreview::class)
@HiltViewModel
class MeterValuesListViewModelImp @Inject constructor(
    private val state: SavedStateHandle,
    private val meterUseCases: MeterUseCases,
    private val prevServiceMeterValuesListConverter: PrevServiceMeterValuesListConverter,
    private val meterValueConverter: MeterValueConverter,
) : MeterValuesListViewModel,
    SingleViewModel<List<MeterValueListItemModel>, UiState<List<MeterValueListItemModel>>, MeterValuesListUiAction, MeterValuesListUiSingleEvent, MeterValueFields, InputsWrapper>(
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

    override fun initState() = UiState.Loading

    override suspend fun handleAction(action: MeterValuesListUiAction): Job {
        Timber.tag(TAG)
            .d("handleAction(MeterValueUiAction) called: %s", action.javaClass.name)
        val job = when (action) {
            is MeterValuesListUiAction.Init -> {
                loadMeterValues()
            }
            is MeterValuesListUiAction.Load -> {
                loadMeterValues(action.payerId)
            }
            is MeterValuesListUiAction.Delete -> {
                saveMeterValue()
            }
            is MeterValuesListUiAction.Save -> {
                saveMeterValue()
            }
        }
        return job
    }

    private fun loadMeterValues(payerId: UUID? = null): Job {
        Timber.tag(TAG)
            .d("loadMeterValues(UUID?) called: payerId = %s", payerId.toString())
        val job = viewModelScope.launch(errorHandler) {
            meterUseCases.getPrevServiceMeterValuesUseCase.execute(
                GetPrevServiceMeterValuesUseCase.Request(payerId)
            ).map {
                prevServiceMeterValuesListConverter.convert(it)
            }.collect {
                submitState(it)
            }
        }
        return job
    }

    private fun saveMeterValue(): Job {
        Timber.tag(TAG).d("saveMeterValue() called")
        val job = viewModelScope.launch(errorHandler) {
            // unsaved values
            currentValue.value.inputs.filter { entry -> !entry.value.isSaved && entry.value.value.isNotEmpty() }
                .forEach { (key, curVal) ->
                    Timber.tag(TAG).d("saveMeterValue(): %s - %s", key, curVal)
                    meterUseCases.saveMeterValueUseCase.execute(
                        SaveMeterValueUseCase.Request(
                            meterValueConverter.toMeterValue(
                                MeterValueListItemModel(
                                    id = UUID.fromString(meterValueId.value.inputs.getValue(key).value),
                                    metersId = UUID.fromString(key),
                                    currentValue = curVal.value.toBigDecimal(),
                                )
                            )
                        )
                    ).collect {
                        when (it) {
                            is Result.Success -> {
                                setStateValue(
                                    field = MeterValueFields.METER_CURR_VALUE,
                                    properties = currentValue,
                                    value = it.data.meterValue.meterValue?.toString() ?: "",
                                    key = it.data.meterValue.metersId.toString(),
                                    isSaved = true
                                )
                            }
                            is Result.Error -> {
                                Timber.tag(TAG).e(it.exception)
                            }
                        }
                    }
                }
        }
        return job
    }

    override fun stateInputFields() = enumValues<MeterValueFields>().map { it.name }

    override fun initFieldStatesByUiModel(uiModel: Any): Job? {
        super.initFieldStatesByUiModel(uiModel)
        val meterValueListItemModel = uiModel as MeterValueListItemModel
        Timber.tag(TAG)
            .d(
                "initFieldStatesByUiModel(MeterValueModel) called: meterValueModel = %s",
                meterValueListItemModel
            )
        meterValueListItemModel.id?.let {
            initStateValue(
                field = MeterValueFields.METER_VALUE_ID, properties = meterValueId,
                value = it.toString(), key = meterValueListItemModel.metersId.toString()
            )
        }
        initStateValue(
            field = MeterValueFields.METERS_ID,
            properties = metersId,
            value = meterValueListItemModel.metersId.toString(),
            key = meterValueListItemModel.metersId.toString()
        )
        initStateValue(
            field = MeterValueFields.METER_CURR_VALUE, properties = currentValue,
            value = meterValueListItemModel.currentValue?.let {
                DecimalFormat(meterValueListItemModel.valueFormat).format(it)
            } ?: "",
            key = meterValueListItemModel.metersId.toString()
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

    override fun clearInputFieldsStates() {
        super.clearInputFieldsStates()
        stateInputFields().forEach {
            Timber.tag(TAG).d("clearInputFieldsStates(): state[%s] = %s", it, state[it])
        }
    }

    override fun onTextFieldFocusChanged(
        focusedField: MeterValueFields, isFocused: Boolean,
        onFocusIn: () -> Unit, onFocusOut: () -> Unit
    ) {
        Timber.tag(TAG).d("onTextFieldFocusChanged() called")
        if (isFocused) {
            onFocusIn()
        } else {
            onFocusOut()
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
        fun previewModel(ctx: Context) =
            object : MeterValuesListViewModel {
                override val uiStateFlow =
                    MutableStateFlow(UiState.Success(previewMeterValueModel(ctx)))
                override val singleEventFlow =
                    Channel<MeterValuesListUiSingleEvent>().receiveAsFlow()
                override val events = Channel<ScreenEvent>().receiveAsFlow()

                override val currentValue = MutableStateFlow(InputsWrapper())
                override val areInputsValid = MutableStateFlow(true)

                override fun initFieldStatesByUiModel(uiModel: Any): Job? = null
                override fun onTextFieldEntered(inputEvent: Inputable) {}
                override fun onTextFieldFocusChanged(
                    focusedField: MeterValueFields, isFocused: Boolean,
                    onFocusIn: () -> Unit, onFocusOut: () -> Unit
                ) {
                }

                override fun clearInputFieldsStates() {}
                override fun onContinueClick(onSuccess: () -> Unit) {}
                override fun submitAction(action: MeterValuesListUiAction): Job? = null
            }

        fun previewMeterValueModel(ctx: Context) =
            listOf(
                MeterValueListItemModel(
                    id = UUID.randomUUID(),
                    metersId = UUID.randomUUID(),
                    type = ServiceType.ELECRICITY,
                    name = ctx.resources.getString(R.string.service_electricity),
                    measureUnit = ctx.resources.getString(com.oborodulin.home.common.R.string.kWh_unit),
                    prevLastDate = Utils.toOffsetDateTime("2022-08-01T14:29:10.212"),
                    prevValue = BigDecimal.valueOf(9628),
                    valueFormat = "#0",
                    valueDate = OffsetDateTime.now()
                ),
                MeterValueListItemModel(
                    id = UUID.randomUUID(),
                    metersId = UUID.randomUUID(),
                    type = ServiceType.COLD_WATER,
                    name = ctx.resources.getString(R.string.service_cold_water),
                    measureUnit = ctx.resources.getString(com.oborodulin.home.common.R.string.m3_unit),
                    prevLastDate = Utils.toOffsetDateTime("2022-08-01T14:29:10.212"),
                    prevValue = BigDecimal.valueOf(1553),
                    valueFormat = "#0.000",
                    valueDate = OffsetDateTime.now()
                )
            )
    }
}
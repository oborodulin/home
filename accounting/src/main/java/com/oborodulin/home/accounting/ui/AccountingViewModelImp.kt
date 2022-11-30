package com.oborodulin.home.accounting.ui

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.oborodulin.home.accounting.domain.usecases.AccountingUseCases
import com.oborodulin.home.accounting.ui.model.AccountingModel
import com.oborodulin.home.accounting.ui.model.converters.PrevServiceMeterValuesConverter
import com.oborodulin.home.common.ui.state.MviViewModel
import com.oborodulin.home.common.ui.state.UiState
import com.oborodulin.home.data.R
import com.oborodulin.home.data.util.ServiceType
import com.oborodulin.home.metering.domain.usecases.GetPrevServiceMeterValuesUseCase
import com.oborodulin.home.metering.ui.model.MeterValueModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

private const val TAG = "Accounting.ui.AccountingViewModel"

@HiltViewModel
class AccountingViewModelImp @Inject constructor(
    private val accountingUseCases: AccountingUseCases,
    private val converter: PrevServiceMeterValuesConverter
) : AccountingViewModel,
    MviViewModel<AccountingModel, UiState<AccountingModel>, AccountingUiAction, AccountingUiSingleEvent>() {
    /*
        private val _uiState = mutableStateOf(
             PayersListUiState(
                payers = listOf(),
                isLoading = true
            )
        )
        val uiState: State<PayersListUiState>
            get() = _uiState
    */
    private val errorHandler = CoroutineExceptionHandler { _, exception ->
        Timber.tag(TAG).e(exception, exception.message)
        //_uiState.value = _uiState.value.copy(error = exception.message, isLoading = false)
    }

    override fun initState(): UiState<AccountingModel> = UiState.Loading

    override fun handleAction(action: AccountingUiAction) {
        Timber.tag(TAG)
            .d("handleAction(AccountingUiAction) called: %s", action.javaClass.name)
        when (action) {
            is AccountingUiAction.Init -> loadPrevServiceMeterValues()
            is AccountingUiAction.Load -> loadPrevServiceMeterValues(action.payerId)
        }
    }

    private fun loadPrevServiceMeterValues(payerId: UUID? = null) {
        Timber.tag(TAG).d("loadPrevServiceMeterValues(UUID?) called: %s", payerId.toString())
        viewModelScope.launch {
            accountingUseCases.getPrevServiceMeterValuesUseCase.execute(
                GetPrevServiceMeterValuesUseCase.Request(payerId)
            ).map {
                converter.convert(it)
            }.collect {
                submitState(it)
            }
        }
    }

    override fun initFieldStatesByUiModel(uiModel: Any) {}


    /*    private fun getPayers() {
            viewModelScope.launch(errorHandler) {
                payerUseCases.getPayersUseCase().collect {
                    _uiState.value = _uiState.value.copy(
                        payers = it,
                        isLoading = false
                    )
                }
            }
        }
    fun onEvent(event: PayersListEvent) {
        when (event) {
            is PayersListEvent.DeletePayer ->
                viewModelScope.launch { payerUseCases.deletePayerUseCase(event.payer) }
        is PayersListEvent.ShowCompletedPayers -> viewModelScope.launch {
            userPreferenceUseCases.updateShowCompleted(event.show)
        }
        is PayersListEvent.ChangeSortByDeadline -> viewModelScope.launch {
            userPreferenceUseCases.enableSortByDeadline(event.enable)
        }
        is PayersListEvent.ChangeSortByPriority -> viewModelScope.launch {
            userPreferenceUseCases.enableSortByPriority(event.enable)
        }


        }
     }

     */
    companion object {
        fun previewModel(ctx: Context) =
            object : AccountingViewModel {
                override val uiStateFlow =
                    MutableStateFlow(UiState.Success(previewAccountingModel(ctx)))
                override val singleEventFlow = Channel<AccountingUiSingleEvent>().receiveAsFlow()

                override fun submitAction(action: AccountingUiAction) {}
            }

        fun previewAccountingModel(ctx: Context) =
            AccountingModel(
                serviceMeterVals = listOf(
                    MeterValueModel(
                        id = UUID.randomUUID(),
                        metersId = UUID.randomUUID(),
                        type = ServiceType.ELECRICITY,
                        name = ctx.resources.getString(R.string.service_electricity),
                        measureUnit = ctx.resources.getString(com.oborodulin.home.common.R.string.kWh_unit),
                        prevLastDate = SimpleDateFormat("yyyy-MM-dd").parse("2022-08-01"),
                        prevValue = BigDecimal.valueOf(9628),
                        valueFormat = "#0",
                        valueDate = Date()
                    ),
                    MeterValueModel(
                        id = UUID.randomUUID(),
                        metersId = UUID.randomUUID(),
                        type = ServiceType.COLD_WATER,
                        name = ctx.resources.getString(R.string.service_cold_water),
                        measureUnit = ctx.resources.getString(com.oborodulin.home.common.R.string.m3_unit),
                        prevLastDate = SimpleDateFormat("yyyy-MM-dd").parse("2022-08-01"),
                        prevValue = BigDecimal.valueOf(1553),
                        valueFormat = "#0.000",
                        valueDate = Date()
                    )
                )
            )
    }
}
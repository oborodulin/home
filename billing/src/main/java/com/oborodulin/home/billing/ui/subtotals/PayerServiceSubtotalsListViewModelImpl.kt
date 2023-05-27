package com.oborodulin.home.billing.ui.subtotals

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.oborodulin.home.billing.domain.usecases.BillingUseCases
import com.oborodulin.home.billing.domain.usecases.GetPayerServiceSubtotalsUseCase
import com.oborodulin.home.billing.ui.model.ServiceSubtotalListItem
import com.oborodulin.home.billing.ui.model.converters.ServiceSubtotalListConverter
import com.oborodulin.home.common.ui.state.MviViewModel
import com.oborodulin.home.common.ui.state.UiState
import com.oborodulin.home.common.util.Utils
import com.oborodulin.home.data.util.ServiceType
import com.oborodulin.home.servicing.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import java.math.BigDecimal
import java.util.*
import javax.inject.Inject

private const val TAG = "Billing.ui.PayerServiceSubtotalsListViewModel"

@HiltViewModel
class PayerServiceSubtotalsListViewModelImpl @Inject constructor(
    private val state: SavedStateHandle,
    private val billingUseCases: BillingUseCases,
    private val serviceSubtotalListConverter: ServiceSubtotalListConverter
) : PayerServiceSubtotalsListViewModel,
    MviViewModel<List<ServiceSubtotalListItem>, UiState<List<ServiceSubtotalListItem>>, PayerServiceSubtotalsListUiAction, PayerServiceSubtotalsListUiSingleEvent>(
        state = state
    ) {

    override fun initState() = UiState.Loading

    override suspend fun handleAction(action: PayerServiceSubtotalsListUiAction): Job {
        Timber.tag(TAG)
            .d("handleAction(PayerServiceSubtotalsListUiAction) called: %s", action.javaClass.name)
        val job = when (action) {
            is PayerServiceSubtotalsListUiAction.Load -> {
                loadPayerServiceSubtotals(action.payerId)
            }
        }
        return job
    }

    private fun loadPayerServiceSubtotals(payerId: UUID): Job {
        Timber.tag(TAG).d("loadPayerServiceSubtotals() called")
        val job = viewModelScope.launch(errorHandler) {
            billingUseCases.getPayerServiceSubtotalsUseCase.execute(
                GetPayerServiceSubtotalsUseCase.Request(payerId)
            ).map {
                serviceSubtotalListConverter.convert(it)
            }
                .collect {
                    submitState(it)
                }
        }
        return job
    }

    override fun initFieldStatesByUiModel(uiModel: Any): Job? = null

    companion object {
        fun previewModel(ctx: Context) =
            object : PayerServiceSubtotalsListViewModel {
                override var primaryObjectData: StateFlow<ArrayList<String>> =
                    MutableStateFlow(arrayListOf())
                override val uiStateFlow = MutableStateFlow(UiState.Success(previewList(ctx)))
                override val singleEventFlow =
                    Channel<PayerServiceSubtotalsListUiSingleEvent>().receiveAsFlow()
                override val actionsJobFlow: SharedFlow<Job?> = MutableSharedFlow()

                //fun viewModelScope(): CoroutineScope = CoroutineScope(Dispatchers.Main)
                override fun handleActionJob(action: () -> Unit, afterAction: () -> Unit) {}
                override fun submitAction(action: PayerServiceSubtotalsListUiAction): Job? = null
                override fun setPrimaryObjectData(value: ArrayList<String>) {}
            }

        fun previewList(ctx: Context) = listOf(
            ServiceSubtotalListItem(
                id = UUID.randomUUID(),
                serviceName = ctx.resources.getString(com.oborodulin.home.data.R.string.service_rent),
                serviceType = ServiceType.RENT,
                isPrivileges = false,
                isAllocateRate = false,
                fromPaymentDate = Utils.toOffsetDateTime("2023-01-01T00:00:00.000+03:00"),
                toPaymentDate = Utils.toOffsetDateTime("2023-02-01T00:00:00.000+03:00"),
                serviceDebt = BigDecimal("485.1")
            ),
            ServiceSubtotalListItem(
                id = UUID.randomUUID(),
                serviceName = ctx.resources.getString(com.oborodulin.home.data.R.string.service_electricity),
                serviceType = ServiceType.ELECTRICITY,
                serviceMeasureUnit = ctx.resources.getString(com.oborodulin.home.common.R.string.kWh_unit),
                serviceDesc = "",
                isPrivileges = false,
                isAllocateRate = true,
                fromPaymentDate = Utils.toOffsetDateTime("2023-01-01T00:00:00.000+03:00"),
                toPaymentDate = Utils.toOffsetDateTime("2023-02-01T00:00:00.000+03:00"),
                diffMeterValue = BigDecimal("96"),
                serviceDebt = BigDecimal("883.2")
            ),
            ServiceSubtotalListItem(
                id = UUID.randomUUID(),
                serviceName = ctx.resources.getString(com.oborodulin.home.data.R.string.service_gas),
                serviceType = ServiceType.GAS,
                isPrivileges = true,
                isAllocateRate = false,
                fromPaymentDate = Utils.toOffsetDateTime("2023-01-01T00:00:00.000+03:00"),
                toPaymentDate = Utils.toOffsetDateTime("2023-02-01T00:00:00.000+03:00"),
                serviceDebt = BigDecimal("36.1")
            ),
            ServiceSubtotalListItem(
                id = UUID.randomUUID(),
                serviceName = ctx.resources.getString(com.oborodulin.home.data.R.string.service_cold_water),
                serviceType = ServiceType.COLD_WATER,
                serviceMeasureUnit = ctx.resources.getString(com.oborodulin.home.common.R.string.m3_unit),
                isPrivileges = false,
                isAllocateRate = false,
                fromPaymentDate = Utils.toOffsetDateTime("2023-01-01T00:00:00.000+03:00"),
                toPaymentDate = Utils.toOffsetDateTime("2023-02-01T00:00:00.000+03:00"),
                diffMeterValue = BigDecimal("15"),
                serviceDebt = BigDecimal("375.3")
            ),
            ServiceSubtotalListItem(
                id = UUID.randomUUID(),
                serviceName = ctx.resources.getString(com.oborodulin.home.data.R.string.service_internet),
                serviceType = ServiceType.INTERNET,
                serviceDesc = ctx.resources.getString(R.string.def_internet_descr),
                isPrivileges = false,
                isAllocateRate = false,
                fromPaymentDate = Utils.toOffsetDateTime("2023-01-01T00:00:00.000+03:00"),
                toPaymentDate = Utils.toOffsetDateTime("2023-02-01T00:00:00.000+03:00"),
                diffMeterValue = BigDecimal("15"),
                serviceDebt = BigDecimal("375.3")
            )
        )
    }
}
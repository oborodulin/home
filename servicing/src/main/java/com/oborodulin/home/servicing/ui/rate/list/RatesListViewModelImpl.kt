package com.oborodulin.home.servicing.ui.rate.list

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.oborodulin.home.common.ui.state.MviViewModel
import com.oborodulin.home.common.ui.state.UiState
import com.oborodulin.home.common.util.Utils
import com.oborodulin.home.presentation.navigation.NavRoutes
import com.oborodulin.home.presentation.navigation.RateInput
import com.oborodulin.home.servicing.domain.usecases.rate.DeleteRateUseCase
import com.oborodulin.home.servicing.domain.usecases.rate.GetRatesUseCase
import com.oborodulin.home.servicing.domain.usecases.rate.RateUseCases
import com.oborodulin.home.servicing.ui.model.RateListItem
import com.oborodulin.home.servicing.ui.model.converters.RatesListConverter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.math.BigDecimal
import java.util.UUID
import javax.inject.Inject

private const val TAG = "Servicing.RatesListViewModelImpl"

@HiltViewModel
class RatesListViewModelImpl @Inject constructor(
    private val state: SavedStateHandle,
    private val rateUseCases: RateUseCases,
    private val ratesListConverter: RatesListConverter
) : RatesListViewModel,
    MviViewModel<List<RateListItem>, UiState<List<RateListItem>>, RatesListUiAction, RatesListUiSingleEvent>(
        state = state
    ) {

    override fun initState() = UiState.Loading

    override suspend fun handleAction(action: RatesListUiAction): Job {
        Timber.tag(TAG)
            .d("handleAction(RatesListUiAction) called: %s", action.javaClass.name)
        val job = when (action) {
            is RatesListUiAction.Load -> {
                loadRates()
            }

            is RatesListUiAction.EditRate -> {
                submitSingleEvent(
                    RatesListUiSingleEvent.OpenRateScreen(
                        NavRoutes.Rate.routeForRate(
                            RateInput(action.rateId)
                        )
                    )
                )
            }

            is RatesListUiAction.DeleteRate -> {
                deleteRate(action.rateId)
            }
        }
        return job
    }

    private fun loadRates(): Job {
        Timber.tag(TAG).d("loadRates() called")
        val job = viewModelScope.launch(errorHandler) {
            rateUseCases.getRatesUseCase.execute(GetRatesUseCase.Request)
                .map {
                    ratesListConverter.convert(it)
                }
                .collect {
                    submitState(it)
                }
        }
        return job
    }

    private fun deleteRate(payerId: UUID): Job {
        Timber.tag(TAG)
            .d("deleteRate() called: payerId = %s", payerId.toString())
        val job = viewModelScope.launch(errorHandler) {
            rateUseCases.deleteRateUseCase.execute(
                DeleteRateUseCase.Request(payerId)
            ).collect {}
        }
        return job
    }

    override fun initFieldStatesByUiModel(uiModel: Any): Job? = null

    companion object {
        fun previewModel(ctx: Context) =
            object : RatesListViewModel {
                override var primaryObjectData: StateFlow<ArrayList<String>> =
                    MutableStateFlow(arrayListOf())
                override val uiStateFlow = MutableStateFlow(UiState.Success(previewList(ctx)))
                override val singleEventFlow = Channel<RatesListUiSingleEvent>().receiveAsFlow()
                override val actionsJobFlow: SharedFlow<Job?> = MutableSharedFlow()

                //fun viewModelScope(): CoroutineScope = CoroutineScope(Dispatchers.Main)
                override fun handleActionJob(action: () -> Unit, afterAction: () -> Unit) {}
                override fun submitAction(action: RatesListUiAction): Job? = null
                override fun setPrimaryObjectData(value: ArrayList<String>) {}
            }

        fun previewList(ctx: Context) = listOf(
            RateListItem(
                id = UUID.randomUUID(),
                serviceId = UUID.randomUUID(),
                startDate = Utils.toOffsetDateTime("2022-08-01T14:29:10.212+03:00"),
                rateValue = BigDecimal("485"),
                isPerPerson = true,
                isPrivileges = false
            ),
            RateListItem(
                id = UUID.randomUUID(),
                serviceId = UUID.randomUUID(),
                startDate = Utils.toOffsetDateTime("2022-08-01T14:29:10.212+03:00"),
                fromMeterValue = BigDecimal.ZERO,
                toMeterValue = BigDecimal("150"),
                rateValue = BigDecimal("1.86"),
                isPerPerson = false,
                isPrivileges = false
            )
        )
    }
}
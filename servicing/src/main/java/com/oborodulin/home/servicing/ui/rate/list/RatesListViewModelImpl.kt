package com.oborodulin.home.servicing.ui.rate.list

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.oborodulin.home.common.ui.state.MviViewModel
import com.oborodulin.home.common.ui.state.UiState
import com.oborodulin.home.common.util.Utils
import com.oborodulin.home.data.R
import com.oborodulin.home.domain.usecases.DeletePayerUseCase
import com.oborodulin.home.domain.usecases.FavoritePayerUseCase
import com.oborodulin.home.presentation.navigation.NavRoutes
import com.oborodulin.home.presentation.navigation.PayerInput
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
import java.util.ArrayList
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class RatesListViewModelImpl @Inject constructor(
    private val state: SavedStateHandle,
    private val payerUseCases: com.oborodulin.home.accounting.domain.usecases.PayerUseCases,
    private val payersListConverter: com.oborodulin.home.accounting.ui.model.converters.PayersListConverter
) : com.oborodulin.home.accounting.ui.payer.list.PayersListViewModel,
    MviViewModel<List<com.oborodulin.home.accounting.ui.model.PayerListItem>, UiState<List<com.oborodulin.home.accounting.ui.model.PayerListItem>>, com.oborodulin.home.accounting.ui.payer.list.PayersListUiAction, com.oborodulin.home.accounting.ui.payer.list.PayersListUiSingleEvent>(
        state = state
    ) {

    override fun initState() = UiState.Loading

    override suspend fun handleAction(action: com.oborodulin.home.accounting.ui.payer.list.PayersListUiAction): Job {
        Timber.tag(com.oborodulin.home.accounting.ui.payer.list.TAG)
            .d("handleAction(PayersListUiAction) called: %s", action.javaClass.name)
        val job = when (action) {
            is com.oborodulin.home.accounting.ui.payer.list.PayersListUiAction.Load -> {
                loadPayers()
            }
            is com.oborodulin.home.accounting.ui.payer.list.PayersListUiAction.EditPayer -> {
                submitSingleEvent(
                    com.oborodulin.home.accounting.ui.payer.list.PayersListUiSingleEvent.OpenPayerScreen(
                        NavRoutes.Payer.routeForPayer(
                            PayerInput(action.payerId)
                        )
                    )
                )
            }
            is com.oborodulin.home.accounting.ui.payer.list.PayersListUiAction.DeletePayer -> {
                deletePayer(action.payerId)
            }
            is com.oborodulin.home.accounting.ui.payer.list.PayersListUiAction.FavoritePayer -> {
                favoritePayer(action.payerId)
            }
            /*is PostListUiAction.UserClick -> {
                updateInteraction(action.interaction)
                submitSingleEvent(
                    PostListUiSingleEvent.OpenUserScreen(
                        NavRoutes.User.routeForUser(
                            UserInput(action.userId)
                        )
                    )
                )
            }*/
        }
        return job
    }

    private fun loadPayers(): Job {
        Timber.tag(com.oborodulin.home.accounting.ui.payer.list.TAG).d("loadPayers() called")
        val job = viewModelScope.launch(errorHandler) {
            payerUseCases.getPayersUseCase.execute(com.oborodulin.home.accounting.domain.usecases.GetPayersUseCase.Request).map {
                payersListConverter.convert(it)
            }
                .collect {
                    submitState(it)
                }
        }
        return job
    }

    private fun deletePayer(payerId: UUID): Job {
        Timber.tag(com.oborodulin.home.accounting.ui.payer.list.TAG)
            .d("deletePayer() called: payerId = %s", payerId.toString())
        val job = viewModelScope.launch(errorHandler) {
            payerUseCases.deletePayerUseCase.execute(
                DeletePayerUseCase.Request(payerId)
            ).collect {}
        }
        return job
    }

    private fun favoritePayer(payerId: UUID): Job {
        Timber.tag(com.oborodulin.home.accounting.ui.payer.list.TAG)
            .d("favoritePayer() called: payerId = %s", payerId.toString())
        val job = viewModelScope.launch(errorHandler) {
            payerUseCases.favoritePayerUseCase.execute(
                FavoritePayerUseCase.Request(payerId)
            ).collect {}
        }
        return job
    }

    override fun initFieldStatesByUiModel(uiModel: Any): Job? = null

    companion object {
        fun previewModel(ctx: Context) =
            object : com.oborodulin.home.accounting.ui.payer.list.PayersListViewModel {
                override var primaryObjectData: StateFlow<ArrayList<String>> =
                    MutableStateFlow(arrayListOf())
                override val uiStateFlow = MutableStateFlow(UiState.Success(previewList(ctx)))
                override val singleEventFlow = Channel<com.oborodulin.home.accounting.ui.payer.list.PayersListUiSingleEvent>().receiveAsFlow()
                override val actionsJobFlow: SharedFlow<Job?> = MutableSharedFlow()

                //fun viewModelScope(): CoroutineScope = CoroutineScope(Dispatchers.Main)
                override fun handleActionJob(action: () -> Unit, afterAction: () -> Unit) {}
                override fun submitAction(action: com.oborodulin.home.accounting.ui.payer.list.PayersListUiAction): Job? = null
                override fun setPrimaryObjectData(value: ArrayList<String>) {}
            }

        fun previewList(ctx: Context) = listOf(
            com.oborodulin.home.accounting.ui.model.PayerListItem(
                id = UUID.randomUUID(),
                fullName = ctx.resources.getString(R.string.def_payer1_full_name),
                address = ctx.resources.getString(R.string.def_payer1_address),
                totalArea = BigDecimal("61"),
                livingSpace = BigDecimal("59"),
                paymentDay = 20,
                personsNum = 2,
                isFavorite = true,
                fromPaymentDate = Utils.toOffsetDateTime("2022-08-01T14:29:10.212+03:00"),
                toPaymentDate = Utils.toOffsetDateTime("2022-09-01T14:29:10.212+03:00"),
                totalDebt = BigDecimal("123456.78")
            ),
            com.oborodulin.home.accounting.ui.model.PayerListItem(
                id = UUID.randomUUID(),
                fullName = ctx.resources.getString(R.string.def_payer2_full_name),
                address = ctx.resources.getString(R.string.def_payer2_address),
                totalArea = BigDecimal("89"),
                livingSpace = BigDecimal("76"),
                paymentDay = 20,
                personsNum = 1,
                fromPaymentDate = Utils.toOffsetDateTime("2022-08-01T14:29:10.212+03:00"),
                toPaymentDate = Utils.toOffsetDateTime("2022-09-01T14:29:10.212+03:00"),
                totalDebt = BigDecimal("876543.21")
            )
        )
    }
}
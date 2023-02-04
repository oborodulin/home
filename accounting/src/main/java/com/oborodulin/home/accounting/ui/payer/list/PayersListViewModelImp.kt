package com.oborodulin.home.accounting.ui.payer.list

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.oborodulin.home.accounting.ui.model.PayerListItemModel
import com.oborodulin.home.accounting.ui.model.converters.PayersListConverter
import com.oborodulin.home.common.ui.state.MviViewModel
import com.oborodulin.home.common.ui.state.UiState
import com.oborodulin.home.domain.usecase.DeletePayerUseCase
import com.oborodulin.home.domain.usecase.FavoritePayerUseCase
import com.oborodulin.home.domain.usecase.GetPayersUseCase
import com.oborodulin.home.domain.usecase.PayerUseCases
import com.oborodulin.home.presentation.navigation.NavRoutes
import com.oborodulin.home.presentation.navigation.PayerInput
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import java.math.BigDecimal
import java.util.*
import javax.inject.Inject

private const val TAG = "Accounting.ui.PayersListViewModel"

@HiltViewModel
class PayersListViewModelImp @Inject constructor(
    private val payerUseCases: PayerUseCases,
    private val payersListConverter: PayersListConverter
) : PayersListViewModel,
    MviViewModel<List<PayerListItemModel>, UiState<List<PayerListItemModel>>, PayersListUiAction, PayersListUiSingleEvent>() {

    override fun initState() = UiState.Loading

    override suspend fun handleAction(action: PayersListUiAction): Job {
        Timber.tag(TAG)
            .d("handleAction(PayersListUiAction) called: %s", action.javaClass.name)
        val job = when (action) {
            is PayersListUiAction.Load -> {
                loadPayers()
            }
            is PayersListUiAction.EditPayer -> {
                submitSingleEvent(
                    PayersListUiSingleEvent.OpenPayerScreen(
                        NavRoutes.Payer.routeForPayer(
                            PayerInput(action.payerId)
                        )
                    )
                )
            }
            is PayersListUiAction.DeletePayer -> {
                deletePayer(action.payerId)
            }
            is PayersListUiAction.FavoritePayer -> {
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
        Timber.tag(TAG).d("loadPayers() called")
        val job = viewModelScope.launch(errorHandler) {
            payerUseCases.getPayersUseCase.execute(GetPayersUseCase.Request).map {
                payersListConverter.convert(it)
            }
                .collect {
                    submitState(it)
                }
        }
        return job
    }

    private fun deletePayer(payerId: UUID): Job {
        Timber.tag(TAG).d("deletePayer() called: payerId = %s", payerId.toString())
        val job = viewModelScope.launch(errorHandler) {
            payerUseCases.deletePayerUseCase.execute(
                DeletePayerUseCase.Request(payerId)
            ).collect {}
        }
        return job
    }

    private fun favoritePayer(payerId: UUID): Job {
        Timber.tag(TAG).d("favoritePayer() called: payerId = %s", payerId.toString())
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
            object : PayersListViewModel {
                override val uiStateFlow = MutableStateFlow(UiState.Success(previewList(ctx)))
                override val singleEventFlow = Channel<PayersListUiSingleEvent>().receiveAsFlow()
                override val actionsJobFlow: SharedFlow<Job?> = MutableSharedFlow()

                //fun viewModelScope(): CoroutineScope = CoroutineScope(Dispatchers.Main)
                override fun handleActionJob(action: () -> Unit, afterAction: () -> Unit) {}
                override fun submitAction(action: PayersListUiAction): Job? = null
            }

        fun previewList(ctx: Context) = listOf(
            PayerListItemModel(
                id = UUID.randomUUID(),
                fullName = ctx.resources.getString(com.oborodulin.home.data.R.string.def_payer1_full_name),
                address = ctx.resources.getString(com.oborodulin.home.data.R.string.def_payer1_address),
                totalArea = BigDecimal.valueOf(61),
                livingSpace = BigDecimal.valueOf(59),
                paymentDay = 20,
                personsNum = 2,
                isFavorite = true,
            ),
            PayerListItemModel(
                id = UUID.randomUUID(),
                fullName = ctx.resources.getString(com.oborodulin.home.data.R.string.def_payer2_full_name),
                address = ctx.resources.getString(com.oborodulin.home.data.R.string.def_payer2_address),
                totalArea = BigDecimal.valueOf(89),
                livingSpace = BigDecimal.valueOf(76),
                paymentDay = 20,
                personsNum = 1,
            )
        )
    }
}
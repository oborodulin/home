package com.oborodulin.home.accounting.ui.payer.list

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.oborodulin.home.accounting.ui.model.PayerListItemModel
import com.oborodulin.home.accounting.ui.model.converters.PayersListConverter
import com.oborodulin.home.common.ui.state.MviViewModel
import com.oborodulin.home.common.ui.state.UiState
import com.oborodulin.home.domain.usecase.GetPayersUseCase
import com.oborodulin.home.domain.usecase.PayerUseCases
import com.oborodulin.home.presentation.navigation.NavRoutes
import com.oborodulin.home.presentation.navigation.PayerInput
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.math.BigDecimal
import java.util.UUID
import javax.inject.Inject

private const val TAG = "Accounting.ui.PayersListViewModel"

@HiltViewModel
class PayersListViewModelImp @Inject constructor(
    private val payerUseCases: PayerUseCases,
    private val converter: PayersListConverter
) : PayersListViewModel,
    MviViewModel<List<PayerListItemModel>, UiState<List<PayerListItemModel>>, PayersListUiAction, PayersListUiSingleEvent>() {

    override fun initState() = UiState.Loading

    override fun handleAction(action: PayersListUiAction) {
        Timber.tag(TAG)
            .d("handleAction(PayersListUiAction) called: %s", action.javaClass.name)
        when (action) {
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
    }

    private fun loadPayers() {
        Timber.tag(TAG).d("loadPayers() called")
        viewModelScope.launch {
            payerUseCases.getPayersUseCase.execute(GetPayersUseCase.Request).map {
                converter.convert(it)
            }
                .collect {
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
            object : PayersListViewModel {
                override val uiStateFlow = MutableStateFlow(UiState.Success(previewList(ctx)))
                override val singleEventFlow = Channel<PayersListUiSingleEvent>().receiveAsFlow()

                override fun submitAction(action: PayersListUiAction) {}
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
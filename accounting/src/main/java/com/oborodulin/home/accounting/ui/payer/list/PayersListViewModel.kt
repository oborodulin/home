package com.oborodulin.home.accounting.ui.payer.list

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
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

private const val TAG = "Accounting.ui.AccountingViewModel"

@HiltViewModel
class PayersListViewModel @Inject constructor(
    private val payerUseCases: PayerUseCases,
    private val converter: PayersListConverter
) : MviViewModel<List<PayerListItemModel>, UiState<List<PayerListItemModel>>, PayersListUiAction, PayersListUiSingleEvent>() {

    override fun initState() = UiState.Loading

    override fun handleAction(action: PayersListUiAction) {
        Timber.tag(TAG).d("handleAction(PayersListUiAction) called: %s".format(action.javaClass.name))
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
}
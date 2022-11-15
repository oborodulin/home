package com.oborodulin.home.accounting.ui.payer.list

import androidx.lifecycle.viewModelScope
import com.oborodulin.home.accounting.R
import com.oborodulin.home.accounting.ui.model.converters.PayersListConverter
import com.oborodulin.home.domain.model.Payer
import com.oborodulin.home.domain.usecase.GetPayersUseCase
import com.oborodulin.home.domain.usecase.PayerUseCases
import com.oborodulin.home.accounting.ui.AccountingViewModel
import com.oborodulin.home.common.ui.navigation.NavRoutes
import com.oborodulin.home.common.ui.navigation.PayerInput
import com.oborodulin.home.common.ui.state.MviViewModel
import com.oborodulin.home.common.ui.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

private const val TAG = "Accounting.AccountingViewModel"

@HiltViewModel
class PayersListViewModel @Inject constructor(
    private val accountingViewModel: AccountingViewModel,
    private val payerUseCases: PayerUseCases,
    private val converter: PayersListConverter
) : MviViewModel<List<Payer>, UiState<List<Payer>>, PayersListUiAction, PayersListUiSingleEvent>() {
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

    override fun initState(): UiState<List<Payer>> = UiState.Loading

    override fun handleAction(action: PayersListUiAction) {
        when (action) {
            is PayersListUiAction.Load -> {
                getPayers()
            }
            is PayersListUiAction.EditPayer -> {
                submitSingleEvent(
                    PayersListUiSingleEvent.OpenPayerDetailScreen(
                        NavRoutes.NavPayerDetailScreen(
                            R.drawable.outline_person_black_24,
                            R.string.nav_item_payer_detail
                        ).routeForPayerDetail(
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

    private fun getPayers() {
        viewModelScope.launch {
            payerUseCases.getPayersUseCase.execute(GetPayersUseCase.Request).map {
                converter.convert(it)
            }
                .collect {
                    submitState(it)
                }
        }
    }

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
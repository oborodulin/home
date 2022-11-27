package com.oborodulin.home.accounting.ui

import androidx.lifecycle.viewModelScope
import com.oborodulin.home.accounting.domain.usecases.AccountingUseCases
import com.oborodulin.home.accounting.ui.model.AccountingModel
import com.oborodulin.home.accounting.ui.model.converters.PrevServiceMeterValuesConverter
import com.oborodulin.home.common.ui.state.MviViewModel
import com.oborodulin.home.common.ui.state.UiState
import com.oborodulin.home.metering.domain.usecases.GetPrevServiceMeterValuesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import javax.inject.Inject

private const val TAG = "Accounting.ui.AccountingViewModel"

@HiltViewModel
class AccountingViewModel @Inject constructor(
    private val accountingUseCases: AccountingUseCases,
    private val converter: PrevServiceMeterValuesConverter
) : MviViewModel<AccountingModel, UiState<AccountingModel>, AccountingUiAction, AccountingUiSingleEvent>() {
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
        Timber.tag(TAG).d("handleAction(AccountingUiAction) called: %s".format(action.javaClass.name))
        when (action) {
            is AccountingUiAction.Init -> loadPrevServiceMeterValues()
            is AccountingUiAction.Load -> loadPrevServiceMeterValues(action.payerId)
        }
    }

    private fun loadPrevServiceMeterValues(payerId: UUID? = null) {
        Timber.tag(TAG).d("loadPrevServiceMeterValues(UUID?) called: %s".format(payerId.toString()))
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
}
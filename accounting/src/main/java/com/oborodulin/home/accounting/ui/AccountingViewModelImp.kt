package com.oborodulin.home.accounting.ui

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import com.oborodulin.home.accounting.domain.usecases.AccountingUseCases
import com.oborodulin.home.accounting.ui.model.AccountingModel
import com.oborodulin.home.common.ui.state.MviViewModel
import com.oborodulin.home.common.ui.state.UiState
import com.oborodulin.home.data.local.db.HomeDatabase
import com.oborodulin.home.metering.ui.model.converters.PrevServiceMeterValuesListConverter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import timber.log.Timber
import javax.inject.Inject

private const val TAG = "Accounting.ui.AccountingViewModel"

@HiltViewModel
class AccountingViewModelImp @Inject constructor(
    private val state: SavedStateHandle,
    private val accountingUseCases: AccountingUseCases,
    private val converter: PrevServiceMeterValuesListConverter
) : AccountingViewModel,
    MviViewModel<AccountingModel, UiState<AccountingModel>, AccountingUiAction, AccountingUiSingleEvent>(
        state = state
    ) {
    override fun initState(): UiState<AccountingModel> = UiState.Loading

    override suspend fun handleAction(action: AccountingUiAction): Job {
        Timber.tag(TAG)
            .d(
                "handleAction(AccountingUiAction) called: %s [HomeDatabase.isImportExecute = %s]",
                action.javaClass.name,
                HomeDatabase.isImportExecute
            )
        if (HomeDatabase.isImportExecute) HomeDatabase.isImportDone?.await()
        Timber.tag(TAG)
            .d(
                "await(): HomeDatabase.isImportExecute = %s; HomeDatabase.isImportDone = %s",
                HomeDatabase.isImportExecute,
                HomeDatabase.isImportDone
            )
        val job = when (action) {
            is AccountingUiAction.Init -> submitState(UiState.Success(data = AccountingModel()))
            //is AccountingUiAction.Load -> loadPrevServiceMeterValues(action.payerId)
        }
        return job
    }

    override fun initFieldStatesByUiModel(uiModel: Any): Job? = null

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
                    MutableStateFlow(
                        UiState.Success(
                            AccountingModel(
                                //serviceMeterVals = previewMeterValueModel(ctx)
                            )
                        )
                    )
                override val singleEventFlow = Channel<AccountingUiSingleEvent>().receiveAsFlow()
//                override val uiMeterValuesState = mutableStateOf<List<MeterValueListItem>>(listOf())

                override fun submitAction(action: AccountingUiAction): Job? {
                    return null
                }
            }
    }
}
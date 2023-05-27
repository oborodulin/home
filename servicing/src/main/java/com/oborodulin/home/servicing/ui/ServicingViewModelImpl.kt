package com.oborodulin.home.servicing.ui

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.oborodulin.home.common.ui.state.MviViewModel
import com.oborodulin.home.common.ui.state.UiState
import com.oborodulin.home.data.R
import com.oborodulin.home.data.local.db.HomeDatabase
import com.oborodulin.home.domain.usecases.GetFavoritePayerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.math.BigDecimal
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ServicingViewModelImpl @Inject constructor(
    private val state: SavedStateHandle,
    private val accountingUseCases: com.oborodulin.home.accounting.domain.usecases.AccountingUseCases,
    private val payerConverter: com.oborodulin.home.accounting.ui.model.converters.FavoritePayerConverter
) : com.oborodulin.home.accounting.ui.AccountingViewModel,
    MviViewModel<com.oborodulin.home.accounting.ui.model.AccountingUi, UiState<com.oborodulin.home.accounting.ui.model.AccountingUi>, com.oborodulin.home.accounting.ui.AccountingUiAction, com.oborodulin.home.accounting.ui.AccountingUiSingleEvent>(
        state = state
    ) {
    override fun initState(): UiState<com.oborodulin.home.accounting.ui.model.AccountingUi> =
        UiState.Loading

    override suspend fun handleAction(action: com.oborodulin.home.accounting.ui.AccountingUiAction): Job {
        Timber.tag(com.oborodulin.home.accounting.ui.TAG)
            .d(
                "handleAction(AccountingUiAction) called: %s [HomeDatabase.isImportExecute = %s]",
                action.javaClass.name,
                HomeDatabase.isImportExecute
            )
        if (HomeDatabase.isImportExecute) HomeDatabase.isImportDone?.await()
        Timber.tag(com.oborodulin.home.accounting.ui.TAG)
            .d(
                "await(): HomeDatabase.isImportExecute = %s; HomeDatabase.isImportDone = %s",
                HomeDatabase.isImportExecute,
                HomeDatabase.isImportDone
            )
        val job = when (action) {
            is com.oborodulin.home.accounting.ui.AccountingUiAction.Init -> loadFavoritePayer()
        }
        return job
    }

    private fun loadFavoritePayer(): Job {
        Timber.tag(com.oborodulin.home.accounting.ui.TAG).d("loadFavoritePayer() called")
        val job = viewModelScope.launch(errorHandler) {
            accountingUseCases.getFavoritePayerUseCase.execute(GetFavoritePayerUseCase.Request)
                .map {
                    payerConverter.convert(it)
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
            object : com.oborodulin.home.accounting.ui.AccountingViewModel {
                override val uiStateFlow =
                    MutableStateFlow(
                        UiState.Success(
                            com.oborodulin.home.accounting.ui.model.AccountingUi(
                                favoritePayer = previewPayerModel(ctx)
                            )
                        )
                    )
                override val singleEventFlow = Channel<com.oborodulin.home.accounting.ui.AccountingUiSingleEvent>().receiveAsFlow()

                override fun submitAction(action: com.oborodulin.home.accounting.ui.AccountingUiAction): Job? {
                    return null
                }
            }

        fun previewPayerModel(ctx: Context) =
            com.oborodulin.home.accounting.ui.model.PayerUi(
                id = UUID.randomUUID(),
                fullName = ctx.resources.getString(R.string.def_payer1_full_name),
                address = ctx.resources.getString(R.string.def_payer1_address),
                totalArea = BigDecimal("61"),
                livingSpace = BigDecimal("59"),
                paymentDay = 20,
                personsNum = 2,
                isFavorite = true,
            )
    }
}
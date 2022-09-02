package com.oborodulin.home.accounting.ui

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.oborodulin.home.accounting.ui.payer.PayersList
import com.oborodulin.home.common.ui.theme.HomeComposableTheme
import timber.log.Timber

/**
 * Created by tfakioglu on 12.December.2021
 */
private const val TAG = "HomeApp.AccountingScreen"

@Composable
fun AccountingScreen() { //setFabOnClick: (() -> Unit) -> Unit
    Timber.tag(TAG).d("AccountingScreen() called")
    val viewModel = hiltViewModel<AccountingViewModel>()
    val state = viewModel.accountingUiState.value

//    val payersList = viewModel.payersList.collectAsLazyPagingItems()

    HomeComposableTheme(darkTheme = true) {
        PayersList(list = state.payers)
    }
    /*LaunchedEffect(Unit) {
        setFabOnClick { println("") }
    }*/
}
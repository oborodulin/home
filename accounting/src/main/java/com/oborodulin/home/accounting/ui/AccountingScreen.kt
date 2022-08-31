package com.oborodulin.home.accounting.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.oborodulin.home.accounting.domain.model.Payer
import com.oborodulin.home.common.ui.components.items.ListItem
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

@Composable
fun PayersList(list: List<Payer>) {
    Timber.tag(TAG).d("PayersList(...) called")
    LazyColumn(modifier = Modifier.background(color = Color.DarkGray)) {
        items(list.size) { index ->
            list[index]?.let { payer ->
                ListItem(
                    icon = null,
                    title = payer.fullName,
                    desc = payer.address
                )
            }
        }
/*
        list.apply {
            val error = when {
                loadState.prepend is LoadState.Error -> loadState.prepend as LoadState.Error
                loadState.append is LoadState.Error -> loadState.append as LoadState.Error
                loadState.refresh is LoadState.Error -> loadState.refresh as LoadState.Error
                else -> null
            }

            val loading = when {
                loadState.prepend is LoadState.Loading -> loadState.prepend as LoadState.Loading
                loadState.append is LoadState.Loading -> loadState.append as LoadState.Loading
                loadState.refresh is LoadState.Loading -> loadState.refresh as LoadState.Loading
                else -> null
            }

            if (loading != null) {
                repeat((0..20).count()) {
                    item {
                        Box(
                            modifier = Modifier
                                .background(color = Color.DarkGray)
                        ) {
                            ShimmerAnimation()
                        }
                    }
                }
            }

            if (error != null) {
                //TODO: add error handler
                item { SweetError(message = error.error.localizedMessage ?: "Error") }
            }
        }*/
    }
}

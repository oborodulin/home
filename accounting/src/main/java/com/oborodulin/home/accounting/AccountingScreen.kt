package com.oborodulin.home.accounting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.oborodulin.home.common.items.ListItem
import com.oborodulin.home.common.loading.ShimmerAnimation
import com.oborodulin.home.common.theme.HomeComposableTheme
import com.oborodulin.home.domain.entity.Payer
import com.talhafaki.composablesweettoast.util.SweetToastUtil.SweetError

/**
 * Created by tfakioglu on 12.December.2021
 */
@Composable
fun AccountingScreen() {
    val viewModel = hiltViewModel<AccountingViewModel>()
    val state = viewModel.state.value

//    val payersList = viewModel.payersList.collectAsLazyPagingItems()

    HomeComposableTheme(darkTheme = true) {
        PayersList(list = state.payers)
    }
}

@Composable
fun PayersList(list: List<Payer>) {
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

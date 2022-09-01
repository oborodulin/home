package com.oborodulin.home.accounting.ui.payer.list

import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.oborodulin.home.accounting.domain.model.Payer
import com.oborodulin.home.common.ui.components.items.ListItem
import timber.log.Timber

private const val TAG = "HomeApp.PayersListView"

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

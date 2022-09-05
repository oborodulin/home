package com.oborodulin.home.accounting.ui.payer

import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import com.oborodulin.home.accounting.R
import com.oborodulin.home.common.ui.components.items.ListItemComponent
import timber.log.Timber

private const val TAG = "PayersListView"

@Composable
fun PayersList(viewModel: PayersListViewModel = hiltViewModel()) {
    val payersUiState = viewModel.uiState.value
    val payers = payersUiState.payers

    Timber.tag(TAG).d("PayersList(...) called")
    LazyColumn(modifier = Modifier.background(color = Color.DarkGray)) {
        items(payers.size) { index ->
            payers[index].let { payer ->
                ListItemComponent(
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
    if (payers.isEmpty()) {
        Text(
            text = stringResource(R.string.payer_list_empty_text),
            style = MaterialTheme.typography.subtitle1,
            fontWeight = FontWeight.Bold
        )
    }
}

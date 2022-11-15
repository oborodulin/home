package com.oborodulin.home.accounting.ui.payer.list

import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.oborodulin.home.accounting.R
import com.oborodulin.home.domain.model.Payer
import com.oborodulin.home.accounting.ui.AccountingUiAction
import com.oborodulin.home.accounting.ui.AccountingViewModel
import com.oborodulin.home.common.ui.components.items.ListItemComponent
import com.oborodulin.home.common.ui.state.CommonScreen
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber

private const val TAG = "PayersListView"

@Composable
fun PayersListView(
    viewModel: PayersListViewModel = hiltViewModel(),
    accountingViewModel: AccountingViewModel = hiltViewModel(),
    navController: NavController
) {
    val context = LocalContext.current
    Timber.tag(TAG).d("PayersListView(...) called")
    LaunchedEffect(Unit) {
        viewModel.submitAction(PayersListUiAction.Load)
    }
    viewModel.uiStateFlow.collectAsState().value.let { state ->
        CommonScreen(state = state) {
            PayersList(it) { payer ->
                accountingViewModel.submitAction(AccountingUiAction.PayerClick(payer.id))
            }
        }
    }
    LaunchedEffect(Unit) {
        viewModel.singleEventFlow.collectLatest {
            when (it) {
                is PayersListUiSingleEvent.OpenPayerDetailScreen -> {
                    Toast.makeText(context, "Open Payer Detail Screen", Toast.LENGTH_SHORT).show()
                    //navController.navigate(it.navRoute)
                }
            }
        }
    }
}

@Composable
fun PayersList(
    payers: List<Payer>,
    onClick: (Payer) -> Unit
) {
    Timber.tag(TAG).d("PayersList(...) called")
    if (payers.isNotEmpty()) {
        LazyColumn(modifier = Modifier.padding(16.dp)) {
            items(payers.size) { index ->
                payers[index].let { payer ->
                    ListItemComponent(
                        icon = null,
                        item = payer
                    ) {
                        onClick(payer)
                    }
                }
            }
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
    else {
        Text(
            text = stringResource(R.string.payer_list_empty_text),
            style = MaterialTheme.typography.subtitle1,
            fontWeight = FontWeight.Bold
        )
    }
}


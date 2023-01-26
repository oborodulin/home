package com.oborodulin.home.accounting.ui.payer.list

import android.content.res.Configuration
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.oborodulin.home.accounting.R
import com.oborodulin.home.accounting.ui.AccountingUiAction
import com.oborodulin.home.accounting.ui.AccountingViewModel
import com.oborodulin.home.accounting.ui.AccountingViewModelImp
import com.oborodulin.home.accounting.ui.model.PayerListItemModel
import com.oborodulin.home.common.ui.components.items.ListItemComponent
import com.oborodulin.home.common.ui.state.CommonScreen
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber

private const val TAG = "Accounting.ui.PayersListView"

@Composable
fun PayersListView(
    viewModel: PayersListViewModel,
    accountingViewModel: AccountingViewModel,
    navController: NavController
) {
    Timber.tag(TAG).d("PayersListView(...) called")
    LaunchedEffect(Unit) {
        Timber.tag(TAG).d("PayersListView: LaunchedEffect() BEFORE collect ui state flow")
        viewModel.submitAction(PayersListUiAction.Load)
    }
    viewModel.uiStateFlow.collectAsState().value.let { state ->
        Timber.tag(TAG).d("Collect ui state flow: %s", state)
        CommonScreen(state = state) {
            PayersList(it,
                onClick = { payer -> accountingViewModel.submitAction(AccountingUiAction.Load(payer.id)) },
                onEdit = { payer -> viewModel.submitAction(PayersListUiAction.EditPayer(payer.id)) }
            ) { payer ->
                viewModel.submitAction(PayersListUiAction.DeletePayer(payer.id))
            }
        }
    }
    LaunchedEffect(Unit) {
        Timber.tag(TAG).d("PayersListView: LaunchedEffect() AFTER collect ui state flow")
        viewModel.singleEventFlow.collectLatest {
            Timber.tag(TAG).d("Collect Latest UiSingleEvent: %s", it.javaClass.name)
            when (it) {
                is PayersListUiSingleEvent.OpenPayerScreen -> {
                    navController.navigate(it.navRoute)
                }
            }
        }
    }
}

@Composable
fun PayersList(
    payers: List<PayerListItemModel>,
    onClick: (PayerListItemModel) -> Unit,
    onEdit: (PayerListItemModel) -> Unit,
    onDelete: (PayerListItemModel) -> Unit
) {
    Timber.tag(TAG).d("PayersList(...) called")
    var selectedIndex by remember {
        mutableStateOf(-1)
    }
    if (payers.isNotEmpty()) {
        val listState = rememberLazyListState()
        LazyColumn(
            state = listState,
            modifier = Modifier
                .padding(8.dp)
                .selectableGroup() // Optional, for accessibility purpose
        ) {
            items(payers.size) { index ->
                payers[index].let { payer ->
                    ListItemComponent(
                        icon = null,
                        item = payer,
                        selected = (payer.isFavorite and (selectedIndex == -1)) or (selectedIndex == index),
                        background = (if (selectedIndex == index) Color.LightGray else Color.Transparent),
                        onClick = {
                            selectedIndex = if (selectedIndex != index) index else -1
                            onClick(payer)
                        },
                        onEdit = { onEdit(payer) }
                    ) {
                        onDelete(payer)
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

@Preview(name = "Night Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Day Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun PreviewPayersList() {
    PayersList(
        payers = PayersListViewModelImp.previewList(LocalContext.current),
        onClick = {},
        onEdit = {},
        onDelete = {})
}

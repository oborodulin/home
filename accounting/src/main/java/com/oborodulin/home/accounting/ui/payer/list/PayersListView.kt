package com.oborodulin.home.accounting.ui.payer.list

import android.content.res.Configuration
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.oborodulin.home.accounting.R
import com.oborodulin.home.accounting.ui.model.PayerListItem
import com.oborodulin.home.common.ui.components.items.ListItemComponent
import com.oborodulin.home.common.ui.state.CommonScreen
import com.oborodulin.home.common.ui.state.SharedViewModel
import com.oborodulin.home.metering.ui.value.MeterValuesListUiAction
import com.oborodulin.home.metering.ui.value.MeterValuesListViewModel
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber

private const val TAG = "Accounting.ui.PayersListView"

@Composable
fun PayersListView(
    viewModel: PayersListViewModel,
    meterValuesListViewModel: MeterValuesListViewModel,
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
                onFavorite = { payer ->
                    viewModel.handleActionJob(action = {
                        viewModel.submitAction(
                            PayersListUiAction.FavoritePayer(payer.id)
                        )
                    },
                        afterAction = {
                            meterValuesListViewModel.submitAction(
                                MeterValuesListUiAction.Load(payer.id)
                            )
                        }
                    )
                },
                onClick = { payer ->
                    viewModel.setPrimaryObjectData(arrayListOf(payer.id.toString(), payer.fullName))
                    with(meterValuesListViewModel) {
                        clearInputFieldsStates()
                        setPrimaryObjectData(arrayListOf(payer.id.toString()))
                        submitAction(MeterValuesListUiAction.Load(payer.id))
                    }
                },
                onEdit = { payer -> viewModel.submitAction(PayersListUiAction.EditPayer(payer.id)) }
            ) { payer ->
                viewModel.handleActionJob(action = {
                    viewModel.submitAction(PayersListUiAction.DeletePayer(payer.id))
                },
                    afterAction = {
                        meterValuesListViewModel.submitAction(MeterValuesListUiAction.Init)
                    }
                )
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
    payers: List<PayerListItem>,
    onFavorite: (PayerListItem) -> Unit,
    onClick: (PayerListItem) -> Unit,
    onEdit: (PayerListItem) -> Unit,
    onDelete: (PayerListItem) -> Unit
) {
    Timber.tag(TAG).d("PayersList(...) called")
    val selectedIndex = remember { mutableStateOf(-1) } // by
    if (payers.isNotEmpty()) {
        val listState = rememberLazyListState()
        LazyColumn(
            state = listState,
            modifier = Modifier
                .selectableGroup() // Optional, for accessibility purpose
                .padding(8.dp)
                .focusable(enabled = true)
        ) {
            items(payers.size) { index ->
                payers[index].let { payer ->
                    ListItemComponent(
                        icon = null,
                        item = payer,
                        selected = (payer.isFavorite and (selectedIndex.value == -1)) or (selectedIndex.value == index),
                        background = (if (selectedIndex.value == index) Color.LightGray else Color.Transparent),
                        deleteDialogText = stringResource(
                            R.string.dlg_confirm_del_payer,
                            payer.fullName
                        ),
                        onFavorite = { onFavorite(payer) },
                        onClick = {
                            selectedIndex.value = if (selectedIndex.value != index) index else -1
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
        onFavorite = {},
        onClick = {},
        onEdit = {},
        onDelete = {})
}

package com.oborodulin.home.servicing.ui.service.list

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
import com.oborodulin.home.accounting.ui.model.ServiceListItem
import com.oborodulin.home.billing.ui.subtotals.ServiceServiceSubtotalsListUiAction
import com.oborodulin.home.billing.ui.subtotals.ServiceServiceSubtotalsListView
import com.oborodulin.home.billing.ui.subtotals.ServiceServiceSubtotalsListViewModel
import com.oborodulin.home.billing.ui.subtotals.ServiceServiceSubtotalsListViewModelImp
import com.oborodulin.home.common.ui.ComponentUiAction
import com.oborodulin.home.common.ui.state.CommonScreen
import com.oborodulin.home.metering.ui.value.MeterValuesListUiAction
import com.oborodulin.home.metering.ui.value.MeterValuesListView
import com.oborodulin.home.metering.ui.value.MeterValuesListViewModel
import com.oborodulin.home.metering.ui.value.MeterValuesListViewModelImp
import com.oborodulin.home.presentation.AppState
import com.oborodulin.home.presentation.components.ServiceListItemComponent
import com.oborodulin.home.presentation.navigation.ServiceInput
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber
import java.util.*

private const val TAG = "Servicing.ui.ServicesListView"

@Composable
fun ServicesListView(
    appState: AppState,
    payersListViewModel: ServicesListViewModelImpl = hiltViewModel(),
    meterValuesListViewModel: MeterValuesListViewModelImp = hiltViewModel(),
    navController: NavController,
    payerInput: ServiceInput
) {
    Timber.tag(TAG).d("ServicesListView(...) called: payerInput = %s", payerInput)
    LaunchedEffect(Unit) {
        Timber.tag(TAG).d("ServicesListView: LaunchedEffect() BEFORE collect ui state flow")
        payersListViewModel.submitAction(ServicesListUiAction.Load)
    }
    payersListViewModel.uiStateFlow.collectAsState().value.let { state ->
        Timber.tag(TAG).d("Collect ui state flow: %s", state)
        CommonScreen(state = state) {
            ServicesAccounting(
                payers = it,
                appState = appState,
                payersListViewModel = payersListViewModel,
                payerServiceSubtotalsListViewModel = payerServiceSubtotalsListViewModel,
                meterValuesListViewModel = meterValuesListViewModel,
                navController = navController,
                payerInput = payerInput
            )
        }
    }
    LaunchedEffect(Unit) {
        Timber.tag(TAG).d("ServicesListView: LaunchedEffect() AFTER collect ui state flow")
        payersListViewModel.singleEventFlow.collectLatest {
            Timber.tag(TAG).d("Collect Latest UiSingleEvent: %s", it.javaClass.name)
            when (it) {
                is ServicesListUiSingleEvent.OpenServiceScreen -> {
                    navController.navigate(it.navRoute)
                }
            }
        }
    }
}

@Composable
fun ServicesList(
    payers: List<ServiceListItem>,
    payerInput: ServiceInput,
    onFavorite: (ServiceListItem) -> Unit,
    onClick: (ServiceListItem) -> Unit,
    onEdit: (ServiceListItem) -> Unit,
    onDelete: (ServiceListItem) -> Unit
) {
    Timber.tag(TAG).d("ServicesList(...) called: payerInput = %s", payerInput)
    var selectedIndex by remember { mutableStateOf(-1) } // by
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
                    val isSelected =
                        ((selectedIndex == -1) and ((payerInput.payerId == payer.id) || payer.isFavorite)) || (selectedIndex == index)
                    ServiceListItemComponent(
                        icon = com.oborodulin.home.presentation.R.drawable.outline_house_black_36,
                        item = payer,
                        itemActions = listOf(
                            ComponentUiAction.EditListItem { onEdit(payer) },
                            ComponentUiAction.DeleteListItem(
                                stringResource(
                                    R.string.dlg_confirm_del_payer,
                                    payer.fullName
                                )
                            ) { onDelete(payer) }),
                        selected = isSelected,
                        background = (if (isSelected) Color.LightGray else Color.Transparent),
                        onFavorite = { onFavorite(payer) },
                    ) {
                        //selectedIndex = if (selectedIndex != index) index else -1
                        if (selectedIndex != index) selectedIndex = index
                        onClick(payer)
                    }
                }
            }
        }
    }
/*
        list.apply {
            val error = when {
                loadState.prepend is LoadState.Error -> loadState.prepend AS LoadState.Error
                loadState.append is LoadState.Error -> loadState.append AS LoadState.Error
                loadState.refresh is LoadState.Error -> loadState.refresh AS LoadState.Error
                else -> null
            }

            val loading = when {
                loadState.prepend is LoadState.Loading -> loadState.prepend AS LoadState.Loading
                loadState.append is LoadState.Loading -> loadState.append AS LoadState.Loading
                loadState.refresh is LoadState.Loading -> loadState.refresh AS LoadState.Loading
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
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ServicesAccounting(
    payers: List<ServiceListItem>,
    appState: AppState,
    payersListViewModel: ServicesListViewModel,
    payerServiceSubtotalsListViewModel: ServiceServiceSubtotalsListViewModel,
    meterValuesListViewModel: MeterValuesListViewModel,
    navController: NavController,
    payerInput: ServiceInput
) {
    Timber.tag(TAG).d("ServicesAccounting(...) called: payerInput = %s", payerInput)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        Box(
            modifier = Modifier
                .padding(vertical = 4.dp)
                .clip(RoundedCornerShape(16.dp))
                //.background(MaterialTheme.colorScheme.background, shape = RoundedCornerShape(20.dp))
                .weight(3.3f)
                .border(
                    2.dp,
                    MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(16.dp)
                )
        ) {
            ServicesList(payers,
                payerInput = payerInput,
                onFavorite = { payer ->
                    payersListViewModel.handleActionJob(action = {
                        payersListViewModel.submitAction(
                            ServicesListUiAction.FavoriteService(payer.id)
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
                    payersListViewModel.setPrimaryObjectData(
                        arrayListOf(
                            payer.id.toString(),
                            payer.fullName
                        )
                    )
                    appState.actionBarSubtitle.value = payer.address
                    with(payerServiceSubtotalsListViewModel) {
                        setPrimaryObjectData(arrayListOf(payer.id.toString()))
                        submitAction(ServiceServiceSubtotalsListUiAction.Load(payer.id))
                    }
                    with(meterValuesListViewModel) {
                        clearInputFieldsStates()
                        setPrimaryObjectData(arrayListOf(payer.id.toString()))
                        submitAction(MeterValuesListUiAction.Load(payer.id))
                    }
                },
                onEdit = { payer ->
                    payersListViewModel.submitAction(
                        ServicesListUiAction.EditService(
                            payer.id
                        )
                    )
                }
            ) { payer ->
                payersListViewModel.handleActionJob(action = {
                    payersListViewModel.submitAction(ServicesListUiAction.DeleteService(payer.id))
                },
                    afterAction = {
                        meterValuesListViewModel.submitAction(MeterValuesListUiAction.Init)
                    }
                )
            }
        }
        Box(
            modifier = Modifier
                .padding(vertical = 4.dp)
                .clip(RoundedCornerShape(16.dp))
                .weight(3.3f)
                .border(
                    2.dp,
                    MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(16.dp)
                )
        ) {
            ServiceServiceSubtotalsListView(
                viewModel = payerServiceSubtotalsListViewModel,
                navController = navController,
                payerInput = payerInput
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .clip(RoundedCornerShape(16.dp))
                .weight(3.4f)
                .border(
                    2.dp,
                    MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(16.dp)
                )
        ) {
            //Text(text = "Итого:")
            MeterValuesListView(
                viewModel = meterValuesListViewModel,
                navController = navController,
                payerInput = payerInput
            )
        }
    }
}

@Preview(name = "Night Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Day Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun PreviewServicesAccounting() {
    ServicesList(
        payers = ServicesListViewModelImp.previewList(LocalContext.current),
        payerInput = ServiceInput(UUID.randomUUID()),
        onFavorite = {},
        onClick = {},
        onEdit = {},
        onDelete = {})
}

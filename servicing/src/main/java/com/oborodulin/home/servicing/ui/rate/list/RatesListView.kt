package com.oborodulin.home.servicing.ui.rate.list

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
import com.oborodulin.home.accounting.ui.model.RateListItem
import com.oborodulin.home.billing.ui.subtotals.RateServiceSubtotalsListUiAction
import com.oborodulin.home.billing.ui.subtotals.RateServiceSubtotalsListView
import com.oborodulin.home.billing.ui.subtotals.RateServiceSubtotalsListViewModel
import com.oborodulin.home.common.ui.ComponentUiAction
import com.oborodulin.home.common.ui.components.items.ListItemComponent
import com.oborodulin.home.common.ui.state.CommonScreen
import com.oborodulin.home.metering.ui.value.MeterValuesListUiAction
import com.oborodulin.home.metering.ui.value.MeterValuesListView
import com.oborodulin.home.metering.ui.value.MeterValuesListViewModel
import com.oborodulin.home.presentation.AppState
import com.oborodulin.home.presentation.components.RateListItemComponent
import com.oborodulin.home.presentation.navigation.RateInput
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber
import java.util.*

private const val TAG = "Servicing.ui.RatesListView"

@Composable
fun RatesListView(
    appState: AppState,
    ratesListViewModel: RatesListViewModelImpl = hiltViewModel(),
    navController: NavController,
    rateInput: RateInput
) {
    Timber.tag(TAG).d("RatesListView(...) called: rateInput = %s", rateInput)
    LaunchedEffect(Unit) {
        Timber.tag(TAG).d("RatesListView: LaunchedEffect() BEFORE collect ui state flow")
        ratesListViewModel.submitAction(RatesListUiAction.Load)
    }
    ratesListViewModel.uiStateFlow.collectAsState().value.let { state ->
        Timber.tag(TAG).d("Collect ui state flow: %s", state)
        CommonScreen(state = state) {
            RatesAccounting(
                appState = appState,
                ratesListViewModel = ratesListViewModel,
                navController = navController,
                rateInput = rateInput
            )
            RatesList(rates = it,
                rateInput = rateInput,
                onFavorite = { rate ->
                    ratesListViewModel.handleActionJob(action = {
                        ratesListViewModel.submitAction(
                            RatesListUiAction.FavoriteRate(rate.id)
                        )
                    },
                        afterAction = {
                            meterValuesListViewModel.submitAction(
                                MeterValuesListUiAction.Load(rate.id)
                            )
                        }
                    )
                },
                onClick = { rate ->
                    ratesListViewModel.setPrimaryObjectData(
                        arrayListOf(
                            rate.id.toString(),
                            rate.fullName
                        )
                    )
                    appState.actionBarSubtitle.value = rate.address
                    with(rateServiceSubtotalsListViewModel) {
                        setPrimaryObjectData(arrayListOf(rate.id.toString()))
                        submitAction(RateServiceSubtotalsListUiAction.Load(rate.id))
                    }
                    with(meterValuesListViewModel) {
                        clearInputFieldsStates()
                        setPrimaryObjectData(arrayListOf(rate.id.toString()))
                        submitAction(MeterValuesListUiAction.Load(rate.id))
                    }
                },
                onEdit = { rate ->
                    ratesListViewModel.submitAction(
                        RatesListUiAction.EditRate(
                            rate.id
                        )
                    )
                }
            ) { rate ->
                ratesListViewModel.handleActionJob(action = {
                    ratesListViewModel.submitAction(RatesListUiAction.DeleteRate(rate.id))
                },
                    afterAction = {
                        meterValuesListViewModel.submitAction(MeterValuesListUiAction.Init)
                    }
                )
            }
        }
    }
    LaunchedEffect(Unit) {
        Timber.tag(TAG).d("RatesListView: LaunchedEffect() AFTER collect ui state flow")
        ratesListViewModel.singleEventFlow.collectLatest {
            Timber.tag(TAG).d("Collect Latest UiSingleEvent: %s", it.javaClass.name)
            when (it) {
                is RatesListUiSingleEvent.OpenRateScreen -> {
                    navController.navigate(it.navRoute)
                }
            }
        }
    }
}

@Composable
fun RatesList(
    rates: List<RateListItem>,
    rateInput: RateInput,
    onFavorite: (RateListItem) -> Unit,
    onClick: (RateListItem) -> Unit,
    onEdit: (RateListItem) -> Unit,
    onDelete: (RateListItem) -> Unit
) {
    Timber.tag(TAG).d("RatesList(...) called: rateInput = %s", rateInput)
    var selectedIndex by remember { mutableStateOf(-1) } // by
    if (rates.isNotEmpty()) {
        val listState = rememberLazyListState()
        LazyColumn(
            state = listState,
            modifier = Modifier
                .selectableGroup() // Optional, for accessibility purpose
                .padding(8.dp)
                .focusable(enabled = true)
        ) {
            items(rates.size) { index ->
                rates[index].let { rate ->
                    val isSelected =
                        ((selectedIndex == -1) and ((rateInput.rateId == rate.id) || rate.isFavorite)) || (selectedIndex == index)
                    ListItemComponent(
                        icon = com.oborodulin.home.presentation.R.drawable.outline_house_black_36,
                        item = rate,
                        itemActions = listOf(
                            ComponentUiAction.EditListItem { onEdit(rate) },
                            ComponentUiAction.DeleteListItem(
                                stringResource(
                                    R.string.dlg_confirm_del_rate,
                                    rate.fullName
                                )
                            ) { onDelete(rate) }),
                        selected = isSelected,
                        background = (if (isSelected) Color.LightGray else Color.Transparent)
                    ) {
                        //selectedIndex = if (selectedIndex != index) index else -1
                        if (selectedIndex != index) selectedIndex = index
                        onClick(rate)
                    }
                }
            }
        }
    }
    else {
        Text(
            text = stringResource(R.string.rate_list_empty_text),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun RatesAccounting(
    rates: List<RateListItem>,
    appState: AppState,
    ratesListViewModel: RatesListViewModel,
    rateServiceSubtotalsListViewModel: RateServiceSubtotalsListViewModel,
    meterValuesListViewModel: MeterValuesListViewModel,
    navController: NavController,
    rateInput: RateInput
) {
    Timber.tag(TAG).d("RatesAccounting(...) called: rateInput = %s", rateInput)
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
            MeterValuesListView(
                viewModel = meterValuesListViewModel,
                navController = navController,
                rateInput = rateInput
            )
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
            RateServiceSubtotalsListView(
                viewModel = rateServiceSubtotalsListViewModel,
                navController = navController,
                rateInput = rateInput
            )
            //Text(text = "Итого:")
        }
    }
}

@Preview(name = "Night Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Day Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun PreviewRatesList() {
    RatesList(
        rates = RatesListViewModelImpl.previewList(LocalContext.current),
        rateInput = RateInput(UUID.randomUUID()),
        onFavorite = {},
        onClick = {},
        onEdit = {},
        onDelete = {})
}

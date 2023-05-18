package com.oborodulin.home.billing.ui.subtotals

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
import com.oborodulin.home.common.ui.ComponentUiAction
import com.oborodulin.home.common.ui.components.items.ListItemComponent
import com.oborodulin.home.common.ui.state.CommonScreen
import com.oborodulin.home.presentation.navigation.PayerInput
import com.oborodulin.home.presentation.util.serviceIconId
import com.oborodulin.home.servicing.R
import com.oborodulin.home.billing.ui.model.ServiceSubtotalListItem
import com.oborodulin.home.presentation.components.ServiceSubtotalListItemComponent
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber

private const val TAG = "Billing.ui.PayerServiceSubtotalsListView"

@Composable
fun PayerServiceSubtotalsListView(
    viewModel: PayerServiceSubtotalsListViewModel,
    navController: NavController,
    payerInput: PayerInput
) {
    Timber.tag(TAG).d("PayerServiceSubtotalsListView(...) called: payerInput = %s", payerInput)
    LaunchedEffect(payerInput.payerId) {
        Timber.tag(TAG)
            .d("PayerServiceSubtotalsListView: LaunchedEffect() BEFORE collect ui state flow")
        viewModel.submitAction(PayerServiceSubtotalsListUiAction.Load(payerInput.payerId))
    }
    viewModel.uiStateFlow.collectAsState().value.let { state ->
        Timber.tag(TAG).d("Collect ui state flow: %s", state)
        CommonScreen(state = state) {
            PayerServiceSubtotalsList(payerServiceSubtotals = it) {println()}
        }
    }
    LaunchedEffect(Unit) {
        Timber.tag(TAG)
            .d("PayerServiceSubtotalsListView: LaunchedEffect() AFTER collect ui state flow")
        viewModel.singleEventFlow.collectLatest {
            Timber.tag(TAG).d("Collect Latest UiSingleEvent: %s", it.javaClass.name)
            when (it) {
                is PayerServiceSubtotalsListUiSingleEvent.OpenPayerServiceScreen -> {
                    navController.navigate(it.navRoute)
                }
            }
        }
    }
}

@Composable
fun PayerServiceSubtotalsList(
    payerServiceSubtotals: List<ServiceSubtotalListItem>,
    onPay: (ServiceSubtotalListItem) -> Unit,
) {
    Timber.tag(TAG).d("PayerServiceSubtotalsList(...) called")
    var selectedIndex by remember { mutableStateOf(-1) }
    if (payerServiceSubtotals.isNotEmpty()) {
        val listState = rememberLazyListState()
        LazyColumn(
            state = listState,
            modifier = Modifier
                .selectableGroup() // Optional, for accessibility purpose
                .padding(8.dp)
                .focusable(enabled = true)
        ) {
            items(payerServiceSubtotals.size) { index ->
                payerServiceSubtotals[index].let { payerServiceSubtotal ->
                    ServiceSubtotalListItemComponent(
                        icon = serviceIconId(payerServiceSubtotal.serviceType),
                        item = payerServiceSubtotal,
                        itemActions = listOf(ComponentUiAction.PayListItem(
                            stringResource(
                                R.string.dlg_confirm_del_payer_service,
                                payerServiceSubtotal.serviceName
                            )
                        ) {
                            onPay(payerServiceSubtotal)
                        }),
                        selected = selectedIndex == index,
                        background = (if (selectedIndex == index) Color.LightGray else Color.Transparent),
                    ) {
                        //selectedIndex = if (selectedIndex != index) index else -1
                        if (selectedIndex != index) selectedIndex = index
                    }
                }
            }
        }
    } else {
        Text(
            text = stringResource(R.string.payer_service_list_empty_text),
            style = MaterialTheme.typography.subtitle1,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(name = "Night Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Day Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun PreviewPayerServiceSubtotalsList() {
    PayerServiceSubtotalsList(
        payerServiceSubtotals = PayerServiceSubtotalsListViewModelImp.previewList(LocalContext.current)
    ) {}
}
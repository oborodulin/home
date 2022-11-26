package com.oborodulin.home.accounting.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.oborodulin.home.accounting.ui.meter.MeterValueView
import com.oborodulin.home.accounting.ui.model.AccountingModel
import com.oborodulin.home.accounting.ui.payer.list.PayersListView
import com.oborodulin.home.common.ui.state.CommonScreen
import com.oborodulin.home.common.ui.theme.HomeComposableTheme
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber

/**
 * Created by tfakioglu on 12.December.2021
 */
private const val TAG = "Accounting.ui.AccountingScreen"

@Composable
fun AccountingScreen(
    navController: NavHostController,
    viewModel: AccountingViewModel = hiltViewModel()
) { //setFabOnClick: (() -> Unit) -> Unit
    Timber.tag(TAG).d("AccountingScreen(...) called")
    LaunchedEffect(Unit) {
        Timber.tag(TAG).d("AccountingScreen: LaunchedEffect() BEFORE collect ui state flow")
        viewModel.submitAction(AccountingUiAction.Load())
    }
    viewModel.uiStateFlow.collectAsState().value.let { state ->
        Timber.tag(TAG).d("Collect ui state flow: %s".format(state))

        HomeComposableTheme(darkTheme = true) {
            CommonScreen(state = state) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colors.surface),
                    horizontalAlignment = Alignment.CenterHorizontally
                )
                {
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(30.dp))
                    ) {
                        Box(
                            modifier = Modifier.weight(2f)
                        ) {
                            PayersListView(navController = navController)
                        }
                        Box(
                            modifier = Modifier.weight(1f)
                        ) {
                            PrevServiceMeterVals(it)
                        }
                    }
                }
                //}
            }
        }
        LaunchedEffect(Unit) {
            Timber.tag(TAG).d("AccountingScreen: LaunchedEffect() AFTER collect ui state flow")
            viewModel.singleEventFlow.collectLatest {
                Timber.tag(TAG).d("Collect Latest UiSingleEvent: %s".format(it.javaClass.name))
                when (it) {
                    is AccountingUiSingleEvent.OpenPayerScreen -> {
                        navController.navigate(it.navRoute)
                    }
                }
            }
        }
    }
}
//val state = viewModel.accountingUiState.value
//    val payersList = viewModel.payersList.collectAsLazyPagingItems()

@Composable
fun PrevServiceMeterVals(accountingModel: AccountingModel) {
    for (meterValue in accountingModel.serviceMeterVals) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            Column {
                Text(text = meterValue.name)
            }
            Column {
                Text(text = meterValue.prevValue.toString())
            }
            Column {
                meterValue.measureUnit?.let { Text(text = it) }
            }
            Column {
                Text(text = meterValue.prevLastDate.toString())
            }
            MeterValueView(meterValueModel = meterValue)
        }
    }
}
/*    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        val (list, bottomControls, noTasksText) = createRefs()
        Text(text = "Элетроэнергия")
    }

 */
/*LaunchedEffect(Unit) {
    setFabOnClick { println("") }
}*/


/*
@Preview(name = "Night Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Day Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun PreviewAccountingScreen() {
    AccountingScreen(navController = rememberNavController())
}
 */


package com.oborodulin.home.accounting.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.oborodulin.home.accounting.ui.meter.MeterValueView
import com.oborodulin.home.accounting.ui.model.AccountingModel
import com.oborodulin.home.accounting.ui.payer.list.PayersListView
import com.oborodulin.home.common.ui.state.CommonScreen
import com.oborodulin.home.common.ui.theme.HomeComposableTheme
import com.oborodulin.home.common.ui.theme.Typography
import com.oborodulin.home.common.util.dateToString
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
        viewModel.submitAction(AccountingUiAction.Init)
    }
    viewModel.uiStateFlow.collectAsState().value.let { state ->
        Timber.tag(TAG).d("Collect ui state flow: %s".format(state))

        HomeComposableTheme() { //darkTheme = true
            CommonScreen(state = state) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colors.surface),
                    horizontalAlignment = Alignment.CenterHorizontally
                )
                {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(30.dp))
                            .weight(3f)
                    ) {
                        PayersListView(navController = navController)
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(30.dp))
                            .weight(1f)
                    ) {
                        PrevServiceMeterVals(it)
                    }
                }
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
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(vertical = 8.dp)
    ) {
        for (meterValue in accountingModel.serviceMeterVals) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.width(90.dp)) {
                    Text(
                        text = meterValue.name,
                        style = Typography.body1.copy(fontWeight = FontWeight.Bold)
                    )
                }
                Column(modifier = Modifier.width(70.dp)) {
                    meterValue.prevLastDate?.let { Text(text = it.dateToString("dd-MM-yy")) }
                }
                Column(modifier = Modifier.width(70.dp)) {
                    meterValue.prevValue?.let { Text(text = it.toString()) }
                }
                Column(modifier = Modifier.width(50.dp)) {
                    meterValue.measureUnit?.let { Text(text = it) }
                }
                MeterValueView(meterValueModel = meterValue)
            }
            Spacer(modifier = Modifier.size(20.dp))
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


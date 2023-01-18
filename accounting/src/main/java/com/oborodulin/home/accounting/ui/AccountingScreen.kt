package com.oborodulin.home.accounting.ui

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.oborodulin.home.accounting.R
import com.oborodulin.home.accounting.ui.meter.MeterValueView
import com.oborodulin.home.accounting.ui.meter.MeterValueViewModel
import com.oborodulin.home.accounting.ui.meter.MeterValueViewModelImp
import com.oborodulin.home.accounting.ui.model.AccountingModel
import com.oborodulin.home.accounting.ui.payer.list.PayersListView
import com.oborodulin.home.accounting.ui.payer.list.PayersListViewModel
import com.oborodulin.home.accounting.ui.payer.list.PayersListViewModelImp
import com.oborodulin.home.common.ui.state.CommonScreen
import com.oborodulin.home.common.ui.state.UiState
import com.oborodulin.home.common.ui.theme.HomeComposableTheme
import com.oborodulin.home.common.ui.theme.Typography
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber
import java.text.DateFormat
import java.text.DecimalFormat
import java.util.*

/**
 * Created by tfakioglu on 12.December.2021
 */
private const val TAG = "Accounting.ui.AccountingScreen"

@Composable
fun AccountingScreen(
    navController: NavHostController,
    viewModel: AccountingViewModelImp = hiltViewModel(),
    payersListViewModel: PayersListViewModelImp = hiltViewModel(),
    meterValueViewModel: MeterValueViewModelImp = hiltViewModel(),
) { //setFabOnClick: (() -> Unit) -> Unit
    Timber.tag(TAG).d("AccountingScreen(...) called")
    LaunchedEffect(Unit) {
        Timber.tag(TAG).d("AccountingScreen: LaunchedEffect() BEFORE collect ui state flow")
        viewModel.submitAction(AccountingUiAction.Init)
    }
    viewModel.uiStateFlow.collectAsState().value.let { state ->
        Timber.tag(TAG).d("Collect ui state flow: %s", state)
        AccountingView(
            state = state,
            navController = navController,
            payersListViewModel = payersListViewModel,
            meterValueViewModel = meterValueViewModel,
        )
        LaunchedEffect(Unit) {
            Timber.tag(TAG).d("AccountingScreen: LaunchedEffect() AFTER collect ui state flow")
            viewModel.singleEventFlow.collectLatest {
                Timber.tag(TAG).d("Collect Latest UiSingleEvent: %s", it.javaClass.name)
                when (it) {
                    is AccountingUiSingleEvent.OpenPayerScreen -> {
                        navController.navigate(it.navRoute)
                    }
                }
            }
        }
    }
}

@Composable
private fun AccountingView(
    state: UiState<AccountingModel>,
    navController: NavHostController,
    payersListViewModel: PayersListViewModel,
    meterValueViewModel: MeterValueViewModel
) {
    Timber.tag(TAG).d("AccountingView(...) called")
    HomeComposableTheme { //(darkTheme = true)
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
                        .weight(5f)
                ) {
                    PayersListView(
                        viewModel = payersListViewModel,
                        accountingViewModel = AccountingViewModelImp.previewModel(LocalContext.current),
                        navController = navController
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(30.dp))
                        .weight(4f)
                ) {
                    //PrevServiceMeterValues(accountingModel = it, viewModel = meterValueViewModel)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(30.dp))
                        .weight(1f)
                ) {

                }
            }
        }
    }
}
//val state = viewModel.accountingUiState.value
//    val payersList = viewModel.payersList.collectAsLazyPagingItems()

@Composable
fun PrevServiceMeterValues(
    accountingModel: AccountingModel,
    viewModel: MeterValueViewModel
) {
    Timber.tag(TAG).d("PrevServiceMeterValues(...) called")
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(vertical = 8.dp)
            .verticalScroll(rememberScrollState())
    ) {
        for (meterValue in accountingModel.serviceMeterVals) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .height(IntrinsicSize.Min),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Column(modifier = Modifier.width(95.dp)) {
                    Text(
                        text = meterValue.name,
                        style = Typography.body1.copy(fontWeight = FontWeight.Bold)
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(80.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    meterValue.prevLastDate?.let {
                        Text(
                            text = DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMAN)
                                .format(it)
                        )
                    }
                    Divider(thickness = 1.dp)
                    Spacer(modifier = Modifier.height(10.dp))
                    meterValue.prevValue?.let {
                        Text(
                            text = DecimalFormat(meterValue.valueFormat).format(it),
                            style = Typography.body1.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(50.dp),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    meterValue.measureUnit?.let { Text(text = it) }
                }
                MeterValueView(meterValueModel = meterValue, viewModel = viewModel)
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Top
                ) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Image(
                        modifier = Modifier
                            .padding(4.dp)
                            .clip(RoundedCornerShape(8.dp))
                        //.clickable { onEdit(item) }
                        ,
                        painter = painterResource(R.drawable.outline_photo_camera_black_24),
                        contentDescription = ""
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Image(
                        modifier = Modifier
                            .padding(4.dp)
                            .clip(RoundedCornerShape(8.dp))
                        //.clickable { onDelete(item) }
                        ,
                        painter = painterResource(com.oborodulin.home.common.R.drawable.outline_delete_black_24),
                        contentDescription = ""
                    )
                }
            }
        }
    }
}

@Preview(name = "Night Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Day Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun PreviewAccountingView() {
    AccountingView(
        state = UiState.Success(AccountingViewModelImp.previewAccountingModel(LocalContext.current)),
        navController = rememberNavController(),
        payersListViewModel = PayersListViewModelImp.previewModel(LocalContext.current),
        meterValueViewModel = MeterValueViewModelImp.previewModel
    )
}

@Preview(name = "Night Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Day Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun PreviewPrevServiceMeterValues() {
    PrevServiceMeterValues(
        accountingModel = AccountingViewModelImp.previewAccountingModel(LocalContext.current),
        viewModel = MeterValueViewModelImp.previewModel
    )
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

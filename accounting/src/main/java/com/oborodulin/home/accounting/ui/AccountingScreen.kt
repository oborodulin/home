package com.oborodulin.home.accounting.ui

import android.content.res.Configuration
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
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
import com.oborodulin.home.common.ui.theme.HomeComposableTheme
import com.oborodulin.home.common.ui.theme.Typography
import com.oborodulin.home.common.util.toast
import com.oborodulin.home.data.util.ServiceType
import com.oborodulin.home.presentation.AppState
import com.oborodulin.home.presentation.components.ScaffoldComponent
import com.oborodulin.home.presentation.navigation.NavRoutes
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber
import java.text.DecimalFormat
import java.time.format.DateTimeFormatter

/**
 * Created by tfakioglu on 12.December.2021
 */
private const val TAG = "Accounting.ui.AccountingScreen"

@Composable
fun AccountingScreen(
    viewModel: AccountingViewModelImp = hiltViewModel(),
    payersListViewModel: PayersListViewModelImp = hiltViewModel(),
    meterValueViewModel: MeterValueViewModelImp = hiltViewModel(),
    appState: AppState,
    nestedScrollConnection: NestedScrollConnection,
    bottomBar: @Composable () -> Unit
) { //setFabOnClick: (() -> Unit) -> Unit
    Timber.tag(TAG).d("AccountingScreen(...) called")
    val context = LocalContext.current
    var showBackButton = false
    /*{
        FabComponent(text = "TODO", onClick = {
            Toast.makeText(context, "Added Todo", Toast.LENGTH_SHORT).show()
        }
        )
    }*/
    LaunchedEffect(Unit) {
        Timber.tag(TAG).d("AccountingScreen: LaunchedEffect() BEFORE collect ui state flow")
        viewModel.submitAction(AccountingUiAction.Init)
    }
/*    LaunchedEffect(navController) {
        navController.currentBackStackEntryFlow.collect { backStackEntry ->
            // You can map the title based on the route using:
            backStackEntry.destination.route?.let {
                currentRoute = it
                actionBarTitle = getTitleByRoute(context, it)
            }
        }
    }
     = when (currentRoute) {
        NavRoutes.Payer.route -> true
        else -> false
    }
    */
    viewModel.uiStateFlow.collectAsState().value.let { state ->
        Timber.tag(TAG).d("Collect ui state flow: %s", state)
        HomeComposableTheme { //(darkTheme = true)
            ScaffoldComponent(
                appState = appState,
                scaffoldState = appState.accountingScaffoldState,
                nestedScrollConnection = nestedScrollConnection,
                topBarTitleId = com.oborodulin.home.presentation.R.string.nav_item_accounting,
                topBarActions = {
                    IconButton(onClick = { appState.commonNavController.navigate(NavRoutes.Payer.routeForPayer()) }) {
                        Icon(Icons.Filled.Add, null)
                    }
                    IconButton(onClick = { context.toast("Settings button clicked...") }) {
                        Icon(Icons.Filled.Settings, null)
                    }
                },
                bottomBar = bottomBar
            ) {
                CommonScreen(paddingValues = it, state = state) { accountingModel ->
                    AccountingView(
                        accountingModel = accountingModel,
                        navController = appState.commonNavController,
                        accountingViewModel = viewModel,
                        payersListViewModel = payersListViewModel,
                        meterValueViewModel = meterValueViewModel
                    )
                }
            }
        }
        LaunchedEffect(Unit) {
            Timber.tag(TAG).d("AccountingScreen: LaunchedEffect() AFTER collect ui state flow")
            viewModel.singleEventFlow.collectLatest {
                Timber.tag(TAG).d("Collect Latest UiSingleEvent: %s", it.javaClass.name)
                when (it) {
                    is AccountingUiSingleEvent.OpenPayerScreen -> {
                        appState.commonNavController.navigate(it.navRoute)
                    }
                }
            }
        }
    }
}

@Composable
private fun AccountingView(
    accountingModel: AccountingModel,
    navController: NavHostController,
    accountingViewModel: AccountingViewModel,
    payersListViewModel: PayersListViewModel,
    meterValueViewModel: MeterValueViewModel
) {
    Timber.tag(TAG).d("AccountingView(...) called")
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colors.surface,
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
                //.background(MaterialTheme.colors.background, shape = RoundedCornerShape(20.dp))
                .weight(5f)
                .border(
                    2.dp,
                    MaterialTheme.colors.primary,
                    shape = RoundedCornerShape(16.dp)
                )
        ) {
            PayersListView(
                viewModel = payersListViewModel,
                accountingViewModel = accountingViewModel,
                navController = navController
            )
        }
        Box(
            modifier = Modifier
                .padding(vertical = 4.dp)
                .clip(RoundedCornerShape(16.dp))
                .weight(4f)
                .border(
                    2.dp,
                    MaterialTheme.colors.primary,
                    shape = RoundedCornerShape(16.dp)
                )
        ) {
            PrevServiceMeterValues(
                accountingModel = accountingModel,
                viewModel = meterValueViewModel
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .clip(RoundedCornerShape(16.dp))
                .weight(1f)
                .border(
                    2.dp,
                    MaterialTheme.colors.primary,
                    shape = RoundedCornerShape(16.dp)
                )
        ) {
            Text(text = "Итого:")
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
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colors.background, shape = RoundedCornerShape(16.dp))
            .verticalScroll(rememberScrollState())
            .padding(vertical = 8.dp)
    ) {
        for (meterValue in accountingModel.serviceMeterVals) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Column(modifier = Modifier.width(95.dp)) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Top
                    ) {
                        Spacer(modifier = Modifier.height(8.dp))
                        ServiceIcon(meterValue.type)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = meterValue.name,
                            style = Typography.body1.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(85.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    meterValue.prevLastDate?.let {
                        Text(
                            text = it.format(DateTimeFormatter.ISO_LOCAL_DATE)
                            //DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMAN).format(it)
                        )
                    }
                    Divider(thickness = 1.dp)
                    Spacer(modifier = Modifier.height(8.dp))
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
                        .width(45.dp),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    meterValue.measureUnit?.let { Text(text = it) }
                }
                MeterValueView(meterValueModel = meterValue, viewModel = viewModel)
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Top
                ) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Image(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .padding(4.dp)
                        //.clickable { onEdit(item) }
                        ,
                        painter = painterResource(R.drawable.outline_photo_camera_black_24),
                        contentDescription = ""
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Image(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .padding(4.dp)
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

@Composable
fun ServiceIcon(serviceType: ServiceType?) =
    when (serviceType) {
        ServiceType.ELECRICITY -> Image(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .padding(4.dp),
            painter = painterResource(com.oborodulin.home.presentation.R.drawable.outline_electric_bolt_black_36),
            contentDescription = ""
        )
        ServiceType.COLD_WATER -> Image(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .padding(4.dp),
            painter = painterResource(com.oborodulin.home.presentation.R.drawable.outline_water_drop_black_36),
            contentDescription = ""
        )
        ServiceType.HOT_WATER -> Image(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .padding(4.dp),
            painter = painterResource(com.oborodulin.home.presentation.R.drawable.outline_opacity_black_36),
            contentDescription = ""
        )
        ServiceType.HEATING -> Image(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .padding(4.dp),
            painter = painterResource(com.oborodulin.home.presentation.R.drawable.ic_radiator_36),
            contentDescription = ""
        )
        else -> {}
    }

@Preview(name = "Night Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Day Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun PreviewAccountingView() {
    AccountingView(
        //UiState.Success(AccountingViewModelImp.previewAccountingModel(LocalContext.current)),
        accountingModel = AccountingModel(),
        navController = rememberNavController(),
        accountingViewModel = AccountingViewModelImp.previewModel(LocalContext.current),
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

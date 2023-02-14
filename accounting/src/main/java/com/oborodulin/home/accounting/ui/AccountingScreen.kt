package com.oborodulin.home.accounting.ui

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.oborodulin.home.accounting.ui.model.AccountingModel
import com.oborodulin.home.accounting.ui.payer.list.PayersListView
import com.oborodulin.home.accounting.ui.payer.list.PayersListViewModel
import com.oborodulin.home.accounting.ui.payer.list.PayersListViewModelImp
import com.oborodulin.home.common.ui.state.CommonScreen
import com.oborodulin.home.common.ui.theme.HomeComposableTheme
import com.oborodulin.home.common.util.toast
import com.oborodulin.home.metering.ui.value.MeterValuesListView
import com.oborodulin.home.metering.ui.value.MeterValuesListViewModel
import com.oborodulin.home.metering.ui.value.MeterValuesListViewModelImp
import com.oborodulin.home.presentation.AppState
import com.oborodulin.home.presentation.components.ScaffoldComponent
import com.oborodulin.home.presentation.navigation.NavRoutes
import com.oborodulin.home.presentation.rememberAppState
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber

/**
 * Created by tfakioglu on 12.December.2021
 */
private const val TAG = "Accounting.ui.AccountingScreen"

@Composable
fun AccountingScreen(
    viewModel: AccountingViewModelImp = hiltViewModel(),
    payersListViewModel: PayersListViewModelImp = hiltViewModel(),
    meterValuesListViewModel: MeterValuesListViewModelImp = hiltViewModel(),
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
                        appState = appState,
                        accountingModel = accountingModel,
                        navController = appState.commonNavController,
                        accountingViewModel = viewModel,
                        payersListViewModel = payersListViewModel,
                        meterValuesListViewModel = meterValuesListViewModel
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
    appState: AppState,
    accountingModel: AccountingModel,
    navController: NavHostController,
    accountingViewModel: AccountingViewModel,
    payersListViewModel: PayersListViewModel,
    meterValuesListViewModel: MeterValuesListViewModel
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
                .border(2.dp, MaterialTheme.colors.primary, shape = RoundedCornerShape(16.dp))
        ) {
            PayersListView(
                appState = appState,
                viewModel = payersListViewModel,
                meterValuesListViewModel = meterValuesListViewModel,
                navController = navController
            )
        }
        Box(
            modifier = Modifier
                .padding(vertical = 4.dp)
                .clip(RoundedCornerShape(16.dp))
                .weight(4f)
                .border(2.dp, MaterialTheme.colors.primary, shape = RoundedCornerShape(16.dp))
        ) {
            MeterValuesListView(
                viewModel = meterValuesListViewModel,
                navController = navController
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .clip(RoundedCornerShape(16.dp))
                .weight(1f)
                .border(2.dp, MaterialTheme.colors.primary, shape = RoundedCornerShape(16.dp))
        ) {
            Text(text = "Итого:")
        }
    }
}

@Preview(name = "Night Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Day Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun PreviewAccountingView() {
    AccountingView(
        appState = rememberAppState(),
        accountingModel = AccountingModel(),
        navController = rememberNavController(),
        accountingViewModel = AccountingViewModelImp.previewModel(LocalContext.current),
        payersListViewModel = PayersListViewModelImp.previewModel(LocalContext.current),
        meterValuesListViewModel = MeterValuesListViewModelImp.previewModel(LocalContext.current)
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

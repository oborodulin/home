package com.oborodulin.home.accounting.ui

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.oborodulin.home.accounting.ui.payer.list.PayersListView
import com.oborodulin.home.common.ui.state.CommonScreen
import com.oborodulin.home.common.ui.theme.HomeComposableTheme
import com.oborodulin.home.common.util.toast
import com.oborodulin.home.presentation.AppState
import com.oborodulin.home.presentation.components.ScaffoldComponent
import com.oborodulin.home.presentation.navigation.NavRoutes
import com.oborodulin.home.presentation.navigation.PayerInput
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber

/**
 * Created by tfakioglu on 12.December.2021
 */
private const val TAG = "Accounting.ui.AccountingScreen"

@Composable
fun AccountingScreen(
    viewModel: AccountingViewModelImp = hiltViewModel(),
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
                    appState.actionBarSubtitle.value = accountingModel.favoritePayer.address
                    viewModel.setPrimaryObjectData(
                        arrayListOf(
                            accountingModel.favoritePayer.id.toString(),
                            accountingModel.favoritePayer.fullName
                        )
                    )
                    PayersListView(
                        appState = appState,
                        navController = appState.commonNavController,
                        payerInput = PayerInput(accountingModel.favoritePayer.id!!)
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

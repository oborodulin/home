package com.oborodulin.home.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.oborodulin.home.accounting.ui.AccountingScreen
import com.oborodulin.home.presentation.AppState
import com.oborodulin.home.presentation.navigation.NavRoutes
import timber.log.Timber

private const val TAG = "App.navigation.NavBarNavigationHost"

@Composable
fun NavBarNavigationHost(
    appState: AppState,
    nestedScrollConnection: NestedScrollConnection,
    bottomBar: @Composable () -> Unit
) {
    Timber.tag(TAG).d("NavBarNavigationHost(...) called")
    NavHost(
        appState.navBarNavController, startDestination = NavRoutes.Accounting.route
    ) {
        composable(NavRoutes.Accounting.route) {
            // Payers; Meters values; Payments
            Timber.tag(TAG)
                .d("Navigation Graph: to AccountingScreen [route = '%s']", it.destination.route)
            AccountingScreen(
                appState = appState,
                nestedScrollConnection = nestedScrollConnection,
                bottomBar = bottomBar
            ) //setFabOnClick = setFabOnClick
        }
        composable(route = NavRoutes.Billing.route, arguments = NavRoutes.Billing.arguments) {
            // Services; Payer services; Rates; Rate promotions
            Timber.tag(TAG)
                .d("Navigation Graph: to BillingScreen [route = '%s']", it.destination.route)
            //BillingScreen(navController)
        }
        composable(route = NavRoutes.Metering.route, arguments = NavRoutes.Metering.arguments) {
            // Meters; Meter verifications
            Timber.tag(TAG)
                .d("Navigation Graph: to MeteringScreen [route = '%s']", it.destination.route)
            //MeteringScreen(navController)
        }
        composable(route = NavRoutes.Reporting.route, arguments = NavRoutes.Reporting.arguments) {
            // Receipts
            Timber.tag(TAG)
                .d("Navigation Graph: to ReportingScreen [route = '%s']", it.destination.route)
            //ReportingScreen(navController)
        }
    }
}

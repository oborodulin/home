package com.oborodulin.home.ui.main

//import com.oborodulin.home.popular.PopularScreen
//import com.oborodulin.home.upcoming.UpcomingScreen
import android.content.res.Configuration
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.oborodulin.home.R
import com.oborodulin.home.accounting.ui.payer.single.PayerScreen
import com.oborodulin.home.presentation.AppState
import com.oborodulin.home.presentation.components.BottomNavigationComponent
import com.oborodulin.home.presentation.navigation.NavRoutes
import com.oborodulin.home.presentation.rememberAppState
import com.oborodulin.home.ui.navigation.NavBarNavigationHost
import timber.log.Timber
import kotlin.math.roundToInt

/**
 * Created by tfakioglu on 12.December.2021
 */
private const val TAG = "App.ui.MainScreen"

@Composable
fun MainScreen() {
    Timber.tag(TAG).d("MainScreen() called")
    val appState = rememberAppState(appName = stringResource(R.string.app_name))

    val bottomBarHeight = 56.dp
    val bottomBarHeightPx = with(LocalDensity.current) {
        bottomBarHeight.roundToPx().toFloat()
    }
    val bottomBarOffsetHeightPx = remember { mutableStateOf(0f) }
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(
                available: Offset,
                source: NestedScrollSource,
            ): Offset {
                val delta = available.y
                val newOffset = bottomBarOffsetHeightPx.value + delta
                bottomBarOffsetHeightPx.value =
                    newOffset.coerceIn(-bottomBarHeightPx, 0f)
                return Offset.Zero
            }
        }
    }
    HomeNavigationHost(appState = appState, nestedScrollConnection = nestedScrollConnection,
        bottomBar = {
            if (appState.shouldShowBottomNavBar) {
                BottomNavigationComponent(
                    modifier = Modifier
                        .height(bottomBarHeight)
                        .offset {
                            IntOffset(
                                x = 0,
                                y = -bottomBarOffsetHeightPx.value.roundToInt()
                            )
                        },
                    appState
                )
            }
        }
    )
}

@Composable
private fun HomeNavigationHost(
    appState: AppState,
    nestedScrollConnection: NestedScrollConnection,
    bottomBar: @Composable () -> Unit
) {
    Timber.tag(TAG).d("HomeNavigationHost(...) called")
    NavHost(appState.navController, startDestination = NavRoutes.Home.route) {
        composable(route = NavRoutes.Payer.routeForPayer()) {
            Timber.tag(TAG)
                .d("Navigation Graph: to PayerScreen [route = '%s']", it.destination.route)
            PayerScreen(navController = appState.navController)
        }
        composable(route = NavRoutes.Payer.route, arguments = NavRoutes.Payer.arguments) {
            Timber.tag(TAG)
                .d("Navigation Graph: to PayerScreen [route = '%s']", it.destination.route)
            PayerScreen(
                navController = appState.navController,
                payerInput = NavRoutes.Payer.fromEntry(it)
            )
        }
        composable(NavRoutes.Home.route) {
            // Dashboard: Payers; Meters values; Payments
            Timber.tag(TAG)
                .d("Navigation Graph: to NavBarNavigationHost [route = '%s']", it.destination.route)
            NavBarNavigationHost(
                appState = appState,
                nestedScrollConnection = nestedScrollConnection,
                bottomBar = bottomBar
            )
        }
    }
}

@Preview(name = "Night Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Day Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun PreviewMainScreen() {
    MainScreen()
}

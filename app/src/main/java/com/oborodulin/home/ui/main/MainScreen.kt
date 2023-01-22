package com.oborodulin.home.ui.main

//import com.oborodulin.home.popular.PopularScreen
//import com.oborodulin.home.upcoming.UpcomingScreen
import android.content.Context
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.oborodulin.home.R
import com.oborodulin.home.accounting.ui.AccountingScreen
import com.oborodulin.home.accounting.ui.payer.single.PayerScreen
import com.oborodulin.home.common.ui.components.FabComponent
import com.oborodulin.home.common.ui.theme.SpeechRed
import com.oborodulin.home.common.util.toast
import com.oborodulin.home.presentation.navigation.NavRoutes
import timber.log.Timber
import kotlin.math.roundToInt

/**
 * Created by tfakioglu on 12.December.2021
 */
private const val TAG = "App.ui.MainScreen"

@Composable
fun MainScreen() {
    Timber.tag(TAG).d("MainScreen() called")
    SettingUpBottomNavigationBarAndCollapsing()
}

@Composable
fun SettingUpBottomNavigationBarAndCollapsing() {
    Timber.tag(TAG).d("SettingUpBottomNavigationBarAndCollapsing() called")
    val context = LocalContext.current
    var actionBarTitle by remember { mutableStateOf(context.getString(R.string.app_name)) }
    var actionBarSubtitle by remember { mutableStateOf("") }

    var currentRoute by remember { mutableStateOf("") }

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

    val scaffoldState = rememberScaffoldState()
    val navController = rememberNavController()

    LaunchedEffect(navController) {
        navController.currentBackStackEntryFlow.collect { backStackEntry ->
            // You can map the title based on the route using:
            backStackEntry.destination.route?.let {
                currentRoute = it
                actionBarTitle = getTitleByRoute(context, it)
            }
        }
    }
    val showBackButton = when (currentRoute) {
        NavRoutes.Payer.route -> true
        else -> false
    }
    Scaffold(modifier = Modifier.nestedScroll(nestedScrollConnection),
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                elevation = 4.dp,
                title = { Text(actionBarTitle) },
                backgroundColor = MaterialTheme.colors.primarySurface,
                navigationIcon = {
                    if (showBackButton) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Filled.ArrowBack, null)
                        }
                    } else {
                        IconButton(onClick = { context.toast("Menu button clicked...") }) {
                            Icon(Icons.Filled.Menu, null)
                        }
                    }
                }, actions = {
                    IconButton(onClick = { navController.navigate(NavRoutes.Payer.route) }) {
                        Icon(Icons.Filled.Add, null)
                    }
                    IconButton(onClick = { context.toast("Settings button clicked...") }) {
                        Icon(Icons.Filled.Settings, null)
                    }
                }
            )
        },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            FabComponent(text = "TODO", onClick = {
                Toast.makeText(context, "Added Todo", Toast.LENGTH_SHORT).show()
            }
            )
        },
        bottomBar = {
            BottomNavigationBar(
                modifier = Modifier
                    .height(bottomBarHeight)
                    .offset { IntOffset(x = 0, y = -bottomBarOffsetHeightPx.value.roundToInt()) },
                navController
            )
        }
    ) {
        MainScreenNavigationConfigurations(
            navController,
            it
        )
    }
}

fun getTitleByRoute(context: Context, route: String): String {
    return when (route) {
        NavRoutes.Accounting.route -> context.getString(NavRoutes.Accounting.titleResId)
        NavRoutes.Payer.route -> context.getString(NavRoutes.Payer.titleResId)
        NavRoutes.Billing.route -> context.getString(NavRoutes.Billing.titleResId)
        NavRoutes.Metering.route -> context.getString(NavRoutes.Metering.titleResId)
        NavRoutes.Reporting.route -> context.getString(NavRoutes.Reporting.titleResId)
        else -> context.getString(R.string.app_name)
    }
}

@Composable
private fun MainScreenNavigationConfigurations(
    navController: NavHostController,
    paddingValues: PaddingValues
) {
    Timber.tag(TAG).d("MainScreenNavigationConfigurations(...) called")
    NavHost(
        navController, startDestination = NavRoutes.Accounting.route,
        modifier = Modifier.padding(paddingValues)
    ) {
        composable(NavRoutes.Accounting.route) {
            // Payers; Meters values; Payments
            AccountingScreen(navController) //setFabOnClick = setFabOnClick
        }
        composable(route = NavRoutes.Payer.route, arguments = NavRoutes.Payer.arguments) {
            PayerScreen(payerInput = NavRoutes.Payer.fromEntry(it))
        }
        composable(route = NavRoutes.Billing.route, arguments = NavRoutes.Billing.arguments) {
            // Services; Payer services; Rates; Rate promotions
            //BillingScreen(navController)
        }
        composable(route = NavRoutes.Metering.route, arguments = NavRoutes.Metering.arguments) {
            // Meters; Meter verifications
            //MeteringScreen(navController)
        }
        composable(route = NavRoutes.Reporting.route, arguments = NavRoutes.Reporting.arguments) {
            // Receipts
            //ReportingScreen(navController)
        }
    }
}

@Composable
fun BottomNavigationBar(
    modifier: Modifier,
    navController: NavController
) {
    Timber.tag(TAG).d("BottomNavigationBar() called")
    val bottomNavRoutes = listOf(
        NavRoutes.Accounting,
        NavRoutes.Billing,
        NavRoutes.Metering,
        NavRoutes.Reporting
    )
    BottomNavigation(
        modifier
            .graphicsLayer {
                shape = RoundedCornerShape(
                    topStart = 20.dp,
                    topEnd = 20.dp
                )
                clip = true
            },
        backgroundColor = colorResource(id = R.color.black),
        contentColor = Color.White
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        bottomNavRoutes.forEach { item ->
            BottomNavigationItem(
                icon = {
                    Icon(
                        painterResource(id = item.iconResId),
                        contentDescription = stringResource(item.titleResId)
                    )
                },
                label = { Text(text = stringResource(item.titleResId)) },
                selectedContentColor = SpeechRed,
                unselectedContentColor = Color.White.copy(0.4f),
                alwaysShowLabel = true,
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) {
                                saveState = true
                            }
                        }

                        /**
                         * As per https://developer.android.com/jetpack/compose/navigation#bottom-nav
                         * By using the saveState and restoreState flags,
                         * the state and back stack of that item is correctly saved
                         * and restored as you swap between bottom navigation items.
                         */

                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true

                        // Restore state when reselecting a previously selected item
                        restoreState = true

                    }
                }
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

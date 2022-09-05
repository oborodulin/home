package com.oborodulin.home.ui.main

import android.widget.Toast
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.oborodulin.home.common.ui.navigation.NavItem
import com.oborodulin.home.common.ui.theme.SpeechRed
import com.oborodulin.home.R
import com.oborodulin.home.accounting.ui.AccountingScreen
import com.oborodulin.home.common.ui.components.FabComponent
import timber.log.Timber
//import com.oborodulin.home.popular.PopularScreen
//import com.oborodulin.home.upcoming.UpcomingScreen
import kotlin.math.roundToInt

/**
 * Created by tfakioglu on 12.December.2021
 */
private const val TAG = "MainScreen"

@Composable
fun MainScreen() {
    Timber.tag(TAG).d("MainScreen() called")
    SettingUpBottomNavigationBarAndCollapsing()
}

@Composable
fun SettingUpBottomNavigationBarAndCollapsing() {
    Timber.tag(TAG).d("SettingUpBottomNavigationBarAndCollapsing() called")

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
    Scaffold(modifier = Modifier.nestedScroll(nestedScrollConnection),
        scaffoldState = scaffoldState,
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            val context = LocalContext.current
            FabComponent(text = "SAVE_TODO", onClick = {
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
        MainScreenNavigationConfigurations(navController, it)
    }
}

@Composable
private fun MainScreenNavigationConfigurations(
    navController: NavHostController,
    paddingValues: PaddingValues
) {
    Timber.tag(TAG).d("MainScreenNavigationConfigurations(...) called")
    NavHost(
        navController, startDestination = NavItem.Accounting.route,
        modifier = Modifier.padding(paddingValues)
    ) {
        composable(NavItem.Accounting.route) {
            AccountingScreen(navController) //setFabOnClick = setFabOnClick
        }
        composable(NavItem.Billing.route) {
            //BillingScreen(navController)
        }
        composable(NavItem.Metering.route) {
            //MeteringScreen(navController)
        }
    }
}

@Composable
fun BottomNavigationBar(modifier: Modifier, navController: NavController) {
    val bottomNavItems = listOf(
        NavItem.Accounting,
        NavItem.Billing,
        NavItem.Metering
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
        bottomNavItems.forEach { item ->
            BottomNavigationItem(
                icon = { Icon(painterResource(id = item.icon), contentDescription = item.title) },
                label = { Text(text = item.title) },
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

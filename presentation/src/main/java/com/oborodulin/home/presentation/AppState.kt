package com.oborodulin.home.presentation

import android.content.res.Resources
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.oborodulin.home.presentation.navigation.NavRoutes
import kotlinx.coroutines.CoroutineScope

/**
 * Remembers and creates an instance of [AppState]
 */
@Composable
fun rememberAppState(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    navController: NavHostController = rememberNavController(),
    navBarNavController: NavHostController = rememberNavController(),
    resources: Resources = LocalContext.current.resources,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    appName: String = ""
) =
    remember(scaffoldState, navController, navBarNavController, resources, coroutineScope) {
        AppState(scaffoldState, navController, navBarNavController, appName)
    }

/**
 * Responsible for holding state related to [App] and containing UI-related logic.
 */
@Stable
class AppState(
    val scaffoldState: ScaffoldState,
    val navController: NavHostController,
    val navBarNavController: NavHostController,
    val appName: String
) {
    // ----------------------------------------------------------
    // Источник состояния BottomBar
    // ----------------------------------------------------------

    private val bottomNavBarTabs = NavRoutes.bottomNavBarRoutes()
    private val bottomNavBarRoutes = bottomNavBarTabs.map { it.route }

    // Атрибут отображения навигационного меню bottomBar
    val shouldShowBottomNavBar: Boolean
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination?.route in bottomNavBarRoutes

    // ----------------------------------------------------------
    // Источник состояния навигации
    // ----------------------------------------------------------

    val navBarCurrentRoute: String?
        get() = navBarNavController.currentDestination?.route

    fun navBarUpPress() {
        navBarNavController.navigateUp()
    }

    // Клик по навигационному меню, вкладке.
    fun navigateToBottomBarRoute(route: String) {
        if (route != navBarCurrentRoute) {
            navBarNavController.navigate(route) {
                // Pop up to the start destination of the graph to
                // avoid building up a large stack of destinations
                // on the back stack as users select items
                //Возвращаем выбранный экран,
                //иначе если backstack не пустой то показываем ранее открытое состяние
                navBarNavController.graph.startDestinationRoute?.let { route ->
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
    }
}
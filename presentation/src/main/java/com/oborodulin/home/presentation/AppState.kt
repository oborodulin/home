package com.oborodulin.home.presentation

import android.content.res.Resources
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.oborodulin.home.presentation.navigation.NavRoutes
import kotlinx.coroutines.CoroutineScope
import timber.log.Timber

/**
 * Remembers and creates an instance of [AppState]
 */
private const val TAG = "Presentation.AppState"

@Composable
fun rememberAppState(
    accountingScaffoldState: ScaffoldState = rememberScaffoldState(),
    payerScaffoldState: ScaffoldState = rememberScaffoldState(),
    commonNavController: NavHostController = rememberNavController(),
    navBarNavController: NavHostController = rememberNavController(),
    resources: Resources = LocalContext.current.resources,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    appName: String = "",
    actionBarSubtitle: MutableState<String> = remember { mutableStateOf("") }
) =
    remember(
        accountingScaffoldState,
        payerScaffoldState,
        commonNavController,
        navBarNavController,
        resources,
        coroutineScope,
        actionBarSubtitle
    ) {
        AppState(
            accountingScaffoldState,
            payerScaffoldState,
            commonNavController,
            navBarNavController,
            appName,
            actionBarSubtitle
        )
    }

/**
 * Responsible for holding state related to [App] and containing UI-related logic.
 */
@Stable
class AppState(
    val accountingScaffoldState: ScaffoldState,
    val payerScaffoldState: ScaffoldState,
    val commonNavController: NavHostController,
    val navBarNavController: NavHostController,
    val appName: String,
    val actionBarSubtitle: MutableState<String>
) {
    // ----------------------------------------------------------
    // Источник состояния TopAppBar
    // ----------------------------------------------------------

    // ----------------------------------------------------------
    // Источник состояния BottomBar
    // ----------------------------------------------------------

    private val bottomNavBarTabs = NavRoutes.bottomNavBarRoutes()
    private val bottomNavBarRoutes = bottomNavBarTabs.map { it.route }

    // Атрибут отображения навигационного меню bottomBar
    val shouldShowBottomNavBar: Boolean
        @Composable get() = commonNavController
            .currentBackStackEntryAsState().value?.destination?.route in bottomNavBarRoutes

    // ----------------------------------------------------------
    // Источник состояния навигации
    // ----------------------------------------------------------

    val navBarCurrentRoute: String?
        get() = this.navBarNavController.currentDestination?.route

    fun navBarUpPress() {
        this.navBarNavController.navigateUp()
    }

    // Возврат к экрану из главного меню нижней панели.
    fun backToBottomBarScreen() {
        Timber.tag(TAG).d("backToBottomBarScreen() called")
        this.commonNavController.popBackStack()
        this.commonNavController.navigate(NavRoutes.Home.route) {
            popUpTo(commonNavController.graph.startDestinationId)
            launchSingleTop = true
        }
    }

    // Клик по навигационному меню, вкладке.
    fun navigateToBottomBarRoute(route: String) {
        if (route != this.navBarCurrentRoute) {
            this.navBarNavController.navigate(route) {
                // Pop up to the start destination of the graph to
                // avoid building up a large stack of destinations
                // on the back stack AS users select items
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
                 * and restored AS you swap between bottom navigation items.
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
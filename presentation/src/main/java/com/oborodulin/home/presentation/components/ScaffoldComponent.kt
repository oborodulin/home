package com.oborodulin.home.presentation.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.oborodulin.home.common.util.toast
import com.oborodulin.home.presentation.AppState
import com.oborodulin.home.presentation.navigation.NavRoutes
import timber.log.Timber

private const val TAG = "Presentation.ScaffoldComponent"

@Composable
fun ScaffoldComponent(
    appState: AppState,
    nestedScrollConnection: NestedScrollConnection,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    content: @Composable (PaddingValues) -> Unit
) {
    Timber.tag(TAG).d("ScaffoldComponent() called")
    val context = LocalContext.current
    var actionBarTitle by remember { mutableStateOf(appState.appName) }
    var actionBarSubtitle by remember { mutableStateOf("") }

/*    LaunchedEffect(appState.navBarNavController) {
        appState.navBarNavController.currentBackStackEntryFlow.collect { backStackEntry ->
            // You can map the title based on the route using:
            backStackEntry.destination.route?.let {
                actionBarTitle = appState.appName + " :: " + NavRoutes.titleByRoute(context, it)
            }
        }
    }

 */
    Scaffold(
        modifier = Modifier.nestedScroll(nestedScrollConnection),
        scaffoldState = appState.scaffoldState,
        topBar = topBar,
        floatingActionButtonPosition = floatingActionButtonPosition,
        floatingActionButton = floatingActionButton,
        bottomBar = bottomBar
    ) {
        content(it)
    }
}

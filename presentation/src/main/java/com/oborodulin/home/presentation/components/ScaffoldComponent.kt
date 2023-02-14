package com.oborodulin.home.presentation.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.oborodulin.home.common.ui.theme.Typography
import com.oborodulin.home.common.util.toast
import com.oborodulin.home.presentation.AppState
import timber.log.Timber

private const val TAG = "Presentation.ScaffoldComponent"

@Composable
fun ScaffoldComponent(
    appState: AppState,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    nestedScrollConnection: NestedScrollConnection? = null,
    @StringRes topBarTitleId: Int? = null,
    topBarNavigationIcon: @Composable (() -> Unit)? = null,
    topBarActions: @Composable RowScope.() -> Unit = {},
    topBar: @Composable (() -> Unit)? = null,
    bottomBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    content: @Composable (PaddingValues) -> Unit
) {
    Timber.tag(TAG).d("ScaffoldComponent() called")
    val context = LocalContext.current
    var actionBarTitle by remember { mutableStateOf(appState.appName) }

/*    LaunchedEffect(appState.navBarNavController) {
        appState.navBarNavController.currentBackStackEntryFlow.collect { backStackEntry ->
            // You can map the title based on the route using:
            backStackEntry.destination.route?.let {
                actionBarTitle = appState.appName + " :: " + NavRoutes.titleByRoute(context, it)
            }
        }
    }

 */
    val modifier = Modifier.fillMaxSize()
    Scaffold(
        modifier = nestedScrollConnection?.let { modifier.nestedScroll(it) } ?: modifier,
        scaffoldState = scaffoldState,
        topBar = {
            when (topBar) {
                null ->
                    TopAppBar(
                        elevation = 4.dp,
                        title = {
                            Column(
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(topBarTitleId?.let {
                                    appState.appName + " - " + stringResource(topBarTitleId)
                                } ?: appState.appName
                                )
                                if (appState.actionBarSubtitle.value.isNotEmpty()) {
                                    Text(
                                        text = appState.actionBarSubtitle.value,
                                        style = Typography.subtitle1
                                    )
                                }
                            }
                        },
                        navigationIcon = {
                            when (topBarNavigationIcon) {
                                null -> IconButton(onClick = { context.toast("Menu button clicked...") }) {
                                    Icon(Icons.Filled.Menu, null)
                                }
                                else -> topBarNavigationIcon()
                            }
                        },
                        actions = topBarActions
                    )
                else -> topBar()
            }
        },
        floatingActionButtonPosition = floatingActionButtonPosition,
        floatingActionButton = floatingActionButton,
        bottomBar = bottomBar
    ) {
        content(it)
    }
}

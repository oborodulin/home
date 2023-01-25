package com.oborodulin.home.presentation.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import com.oborodulin.home.common.ui.theme.SpeechRed
import com.oborodulin.home.presentation.AppState
import com.oborodulin.home.presentation.navigation.NavRoutes
import timber.log.Timber

private const val TAG = "Presentation.BottomNavBarComponent"

@Composable
fun BottomNavigationComponent(modifier: Modifier, appState: AppState) {
    Timber.tag(TAG).d("BottomNavigationBar(...) called")
    BottomNavigation(
        modifier
            .graphicsLayer {
                shape = RoundedCornerShape(
                    topStart = 20.dp,
                    topEnd = 20.dp
                )
                clip = true
            },
        backgroundColor = Color.Black, //colorResource(id = R.color.black), //MaterialTheme.colors.background,
        contentColor = Color.White //MaterialTheme.colors.contentColorFor(MaterialTheme.colors.background)
    ) {
        val navBackStackEntry by appState.navBarNavController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        NavRoutes.bottomNavBarRoutes().forEach { item ->
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
                onClick = { appState.navigateToBottomBarRoute(item.route) }
            )
        }
    }
}

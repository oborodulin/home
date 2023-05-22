package com.oborodulin.home.presentation.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import com.oborodulin.home.presentation.AppState
import com.oborodulin.home.presentation.navigation.NavRoutes
import timber.log.Timber

private const val TAG = "Presentation.BottomNavBarComponent"

@Composable
fun BottomNavigationComponent(modifier: Modifier, appState: AppState) {
    Timber.tag(TAG).d("BottomNavigationBar(...) called")
    NavigationBar(
        modifier
            .graphicsLayer {
                shape = RoundedCornerShape(
                    topStart = 20.dp,
                    topEnd = 20.dp
                )
                clip = true
            },
        containerColor = Color.Black, //colorResource(id = R.color.black), //MaterialTheme.colorScheme.background,
        contentColor = Color.White //MaterialTheme.colorScheme.contentColorFor(MaterialTheme.colorScheme.background)
    ) {
        val navBackStackEntry by appState.navBarNavController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        NavRoutes.bottomNavBarRoutes().forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        painterResource(id = item.iconResId),
                        contentDescription = stringResource(item.titleResId)
                    )
                },
                label = { Text(text = stringResource(item.titleResId)) },
                //colors = NavigationBarItemDefaults.colors(selectedIconColor = SpeechRed,)
                //unselectedContentColor = Color.White.copy(0.4f),
                alwaysShowLabel = true,
                selected = currentRoute == item.route,
                onClick = { appState.navigateToBottomBarRoute(item.route) }
            )
        }
    }
}

package com.felipimatheuz.primehunt.ui.navigation

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.felipimatheuz.primehunt.business.resources.AppSettings
import com.felipimatheuz.primehunt.business.state.BottomNavItem
import com.felipimatheuz.primehunt.ui.theme.WarframeprimehuntTheme

@Composable
fun BottomNav(navController: NavController) {
    BottomNavigation(
        backgroundColor = MaterialTheme.colorScheme.secondary,
        contentColor = MaterialTheme.colorScheme.onSecondary
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route.toString().split("/")[0]
        val context = LocalContext.current
        BottomNavItem.getList().forEach { item ->
            BottomNavigationItem(
                icon = { Icon(painterResource(id = item.icon), contentDescription = null) },
                label = {
                    Text(
                        text = stringResource(item.title),
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                selectedContentColor = MaterialTheme.colorScheme.onSecondary,
                unselectedContentColor = MaterialTheme.colorScheme.onSecondary.copy(0.4f),
                alwaysShowLabel = true,
                selected = currentRoute == item.screenRoute.split("/")[0],
                onClick = {
                    val filter = AppSettings(context).getPrimeFilter(item.filter)
                    navController.navigate(item.screenRoute.replace("{filter}", filter.toString())) {

                        navController.graph.startDestinationRoute?.let { screenRoute ->
                            popUpTo(screenRoute) {
                                saveState = true
                            }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Preview
@Composable
fun BottoMNavPreview() {
    WarframeprimehuntTheme {
        BottomNav(rememberNavController())
    }
}
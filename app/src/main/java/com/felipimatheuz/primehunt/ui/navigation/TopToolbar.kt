package com.felipimatheuz.primehunt.ui.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.business.resources.AppSettings
import com.felipimatheuz.primehunt.business.state.BottomNavItem
import com.felipimatheuz.primehunt.business.state.MenuDialogState
import com.felipimatheuz.primehunt.business.util.PrimeFilter
import com.felipimatheuz.primehunt.ui.component.PrimeInfoDialog
import com.felipimatheuz.primehunt.ui.screen.SyncAccountScreen
import com.felipimatheuz.primehunt.ui.theme.WarframeprimehuntTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopToolbar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val item =
        BottomNavItem.getList()
            .firstOrNull { navBackStackEntry?.destination?.route == it.screenRoute }
    var expanded by remember { mutableStateOf(false) }
    var showInfo by remember { mutableStateOf<MenuDialogState>(MenuDialogState.None) }
    val context = LocalContext.current
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(
                        id =
                        if (item != null) {
                            R.drawable.regal_aya
                        } else {
                            R.mipmap.ic_launcher_adaptive_fore
                        }
                    ), contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .padding(end = 8.dp)
                )
                Text(
                    text = stringResource(
                        item?.title ?: R.string.app_name
                    ), color = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        actions = {
            if (item != null) {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        painterResource(
                            if (item.title == R.string.menu_overview) {
                                R.drawable.ic_menu
                            } else {
                                R.drawable.filter
                            }
                        ),
                        contentDescription = stringResource(R.string.filter),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }

                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    if (item.title == R.string.menu_overview) {
                        val dialogState =
                            listOf(MenuDialogState.Help, MenuDialogState.Info, MenuDialogState.Sync)
                        dialogState.forEach {
                            DropdownMenuItem(
                                leadingIcon = {
                                    Icon(
                                        painter = painterResource(it.icon),
                                        contentDescription = stringResource(it.title)
                                    )
                                },
                                text = { Text(text = stringResource(it.title)) },
                                onClick = {
                                    expanded = false
                                    showInfo = it
                                }
                            )
                        }
                    } else {
                        var filterState = PrimeFilter.entries.toTypedArray()
                        if (item.icon == R.drawable.ic_lato_prime) {
                            filterState =
                                filterState.filter { it != PrimeFilter.AVAILABLE && it != PrimeFilter.UNAVAILABLE }
                                    .toTypedArray()
                        }
                        filterState.forEach {
                            DropdownMenuItem(
                                text = { Text(text = stringResource(it.text)) },
                                onClick = {
                                    expanded = false
                                    AppSettings(context).setPrimeFilter(item.filter, it)
                                    navController.navigate(
                                        item.screenRoute.replace(
                                            "{filter}",
                                            it.toString()
                                        )
                                    )
                                }
                            )
                        }
                    }
                }
            }
        },
        colors = topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
    )
    if (showInfo == MenuDialogState.Sync) {
        SyncAccountScreen(showInfo) { showInfo = MenuDialogState.None }
    } else if (showInfo != MenuDialogState.None) {
        PrimeInfoDialog(showInfo) { showInfo = MenuDialogState.None }
    }
}


@Preview
@Composable
fun TopToolbarPreview() {
    WarframeprimehuntTheme {
        TopToolbar(rememberNavController())
    }
}
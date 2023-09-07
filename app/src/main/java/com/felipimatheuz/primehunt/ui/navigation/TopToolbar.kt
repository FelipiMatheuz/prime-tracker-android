package com.felipimatheuz.primehunt.ui.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.state.BottomNavItem
import com.felipimatheuz.primehunt.state.MenuDialogState
import com.felipimatheuz.primehunt.ui.component.PrimeInfoDialog
import com.felipimatheuz.primehunt.ui.theme.WarframeprimehuntTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopToolbar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val item =
        BottomNavItem.getList().firstOrNull { navBackStackEntry?.destination?.route == it.screenRoute }
    var expanded by remember { mutableStateOf(false) }
    var showInfo by remember { mutableStateOf<MenuDialogState>(MenuDialogState.None) }
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
                    modifier = Modifier.size(40.dp).padding(end = 8.dp)
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
                        val dialogState = listOf(MenuDialogState.Help, MenuDialogState.Info)
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
                        //TODO filter
                    }
                }
            }
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
    )
    if (showInfo != MenuDialogState.None) {
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
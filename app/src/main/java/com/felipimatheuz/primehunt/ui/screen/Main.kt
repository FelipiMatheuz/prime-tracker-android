package com.felipimatheuz.primehunt.ui.screen

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.rememberNavController
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.business.state.MenuDialogState
import com.felipimatheuz.primehunt.ui.component.PrimeInfoDialog
import com.felipimatheuz.primehunt.ui.navigation.BottomNav
import com.felipimatheuz.primehunt.ui.navigation.NavigationGraph
import com.felipimatheuz.primehunt.ui.navigation.TopToolbar

@Composable
fun MainScreen() {
    var finishSplash by rememberSaveable { mutableStateOf(false) }
    var showMigrationWarning by rememberSaveable(inputs = arrayOf(Unit)) { mutableStateOf(true) }
    var showInfo by remember { mutableStateOf<MenuDialogState>(MenuDialogState.None) }

    val navController = rememberNavController()
    if (finishSplash) {
        Scaffold(
            topBar = {
                TopToolbar(
                    navController = navController,
                    onShowInfoChange = { showInfo = it }
                )
            },
            bottomBar = {
                BottomNav(navController = navController)
            }
        ) {
            NavigationGraph(navController = navController, padding = it)

            if (showMigrationWarning) {
                MigrationWarningDialog(
                    onDismiss = { showMigrationWarning = false }
                )
            }

            if (showInfo == MenuDialogState.Sync) {
                SyncAccountScreen(showInfo) { showInfo = MenuDialogState.None }
            } else if (showInfo != MenuDialogState.None) {
                PrimeInfoDialog(showInfo) { showInfo = MenuDialogState.None }
            }
        }
    } else {
        SplashScreen { finishSplash = true }
    }
}

@Composable
fun MigrationWarningDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.migration_warning_title)) },
        text = { Text(text = stringResource(R.string.migration_warning_message)) },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.close))
            }
        }
    )
}

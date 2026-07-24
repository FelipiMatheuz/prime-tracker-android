package com.felipimatheuz.primehunt.ui.screen

import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.felipimatheuz.primehunt.business.util.PrimeFilter
import com.felipimatheuz.primehunt.ui.navigation.*
import kotlinx.coroutines.launch

@Composable
fun MainScreen() {
    var finishSplash by rememberSaveable { mutableStateOf(false) }

    if (finishSplash) {
        val navState = rememberNavigationState(
            startRoute = OverviewKey,
            topLevelRoutes = setOf(
                OverviewKey,
                PrimeSetsKey(),
                OtherPrimesKey(),
                RelicsKey(),
                SyncKey,
                HelpKey,
                AboutKey
            )
        )
        val navigator = remember(navState) { Navigator(navState) }
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scope = rememberCoroutineScope()

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    DrawerContent(
                        currentKey = navState.topLevelRoute as AppNavKey,
                        onKeySelected = { key ->
                            navigator.navigate(key)
                            scope.launch { drawerState.close() }
                        }
                    )
                }
            }
        ) {
            Scaffold(
                topBar = {
                    TopToolbar(
                        currentKey = navState.topLevelRoute as AppNavKey,
                        onMenuClick = { scope.launch { drawerState.open() } }
                    )
                }
            ) { padding ->
                NavDisplay(
                    entries = navState.toEntries { key ->
                        NavEntry(key) {
                            when (key) {
                                OverviewKey -> OverviewScreen(padding, true, {})
                                is PrimeSetsKey -> PrimeSetRoute(padding)
                                is OtherPrimesKey -> OtherPrimeScreen(padding, PrimeFilter.valueOf(key.filter))
                                is RelicsKey -> RelicScreen(padding, PrimeFilter.valueOf(key.filter))
                                SyncKey -> SyncAccountScreen()
                                HelpKey -> HelpScreen()
                                AboutKey -> AboutScreen()
                                else -> Text("Unknown Route")
                            }
                        }
                    },
                    onBack = { navigator.goBack() }
                )
            }
        }
    } else {
        SplashScreen({ finishSplash = true })
    }
}

@Composable
fun DrawerContent(
    currentKey: AppNavKey,
    onKeySelected: (AppNavKey) -> Unit
) {
    val items = listOf(
        OverviewKey to "Overview",
        PrimeSetsKey() to "Prime Sets",
        OtherPrimesKey() to "Other Primes",
        RelicsKey() to "Relics",
        SyncKey to "Sync",
        HelpKey to "Help",
        AboutKey to "About"
    )

    items.forEach { (key, label) ->
        NavigationDrawerItem(
            label = { Text(label) },
            selected = currentKey::class == key::class,
            onClick = { onKeySelected(key) }
        )
    }
}

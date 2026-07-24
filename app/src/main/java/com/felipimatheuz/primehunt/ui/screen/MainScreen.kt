package com.felipimatheuz.primehunt.ui.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.felipimatheuz.primehunt.business.util.PrimeFilter
import com.felipimatheuz.primehunt.ui.navigation.AboutKey
import com.felipimatheuz.primehunt.ui.navigation.AppNavKey
import com.felipimatheuz.primehunt.ui.navigation.HelpKey
import com.felipimatheuz.primehunt.ui.navigation.Navigator
import com.felipimatheuz.primehunt.ui.navigation.OtherPrimesKey
import com.felipimatheuz.primehunt.ui.navigation.OverviewKey
import com.felipimatheuz.primehunt.ui.navigation.PrimeSetsKey
import com.felipimatheuz.primehunt.ui.navigation.RelicsKey
import com.felipimatheuz.primehunt.ui.navigation.SyncKey
import com.felipimatheuz.primehunt.ui.navigation.TopToolbar
import com.felipimatheuz.primehunt.ui.navigation.rememberNavigationState
import com.felipimatheuz.primehunt.ui.navigation.toEntries
import kotlinx.coroutines.launch

@Composable
fun MainScreen() {
    var finishSplash by rememberSaveable { mutableStateOf(false) }

    if (finishSplash) {
        MainContent()
    } else {
        SplashScreen({ finishSplash = true })
    }
}

@Composable
fun MainContent() {
    val navState = rememberNavigationState(
        startRoute = OverviewKey,
        topLevelRoutes = AppNavKey.topLevelRoutes
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
                        when (val appNavKey = key as AppNavKey) {
                            OverviewKey -> OverviewScreen(padding, true, {})
                            is PrimeSetsKey -> PrimeSetRoute(padding)
                            is OtherPrimesKey -> OtherPrimeScreen(
                                padding,
                                PrimeFilter.valueOf(appNavKey.filter)
                            )

                            is RelicsKey -> RelicScreen(
                                padding,
                                PrimeFilter.valueOf(appNavKey.filter)
                            )

                            SyncKey -> SyncAccountScreen(padding)
                            HelpKey -> HelpScreen(padding)
                            AboutKey -> AboutScreen(padding)
                        }
                    }
                },
                onBack = { navigator.goBack() }
            )
        }
    }
}

@Composable
fun DrawerContent(
    currentKey: AppNavKey,
    onKeySelected: (AppNavKey) -> Unit
) {
    AppNavKey.topLevelRoutes.forEach { appNavKey ->
        NavigationDrawerItem(
            modifier = Modifier.padding(8.dp),
            label = { Text(stringResource(appNavKey.label)) },
            icon = {
                Icon(
                    painter = painterResource(appNavKey.icon),
                    contentDescription = null
                )
            },
            selected = currentKey::class == appNavKey::class,
            onClick = { onKeySelected(appNavKey) }
        )
    }
}

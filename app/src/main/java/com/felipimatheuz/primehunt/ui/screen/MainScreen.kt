package com.felipimatheuz.primehunt.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.ui.navigation.AboutKey
import com.felipimatheuz.primehunt.ui.navigation.AppNavKey
import com.felipimatheuz.primehunt.ui.navigation.GoalDetailKey
import com.felipimatheuz.primehunt.ui.navigation.GoalsKey
import com.felipimatheuz.primehunt.ui.navigation.HelpKey
import com.felipimatheuz.primehunt.ui.navigation.NavTransitions.calculatePopTransition
import com.felipimatheuz.primehunt.ui.navigation.NavTransitions.calculateTransition
import com.felipimatheuz.primehunt.ui.navigation.Navigator
import com.felipimatheuz.primehunt.ui.navigation.NewGoalKey
import com.felipimatheuz.primehunt.ui.navigation.OverviewKey
import com.felipimatheuz.primehunt.ui.navigation.PrimeDetailKey
import com.felipimatheuz.primehunt.ui.navigation.PrimeSetsKey
import com.felipimatheuz.primehunt.ui.navigation.RelicsKey
import com.felipimatheuz.primehunt.ui.navigation.SyncKey
import com.felipimatheuz.primehunt.ui.navigation.TopToolbar
import com.felipimatheuz.primehunt.ui.navigation.rememberNavigationState
import com.felipimatheuz.primehunt.ui.navigation.toEntries
import com.felipimatheuz.primehunt.ui.screen.components.PrimeBackgroundPattern
import com.felipimatheuz.primehunt.ui.screen.goals.GoalsScreen
import com.felipimatheuz.primehunt.ui.screen.goals.manage.ManageGoalScreen
import com.felipimatheuz.primehunt.ui.screen.overview.OverviewScreen
import com.felipimatheuz.primehunt.ui.screen.primeset.PrimeSetScreen
import com.felipimatheuz.primehunt.ui.screen.primeset.primedetail.PrimeDetailScreen
import com.felipimatheuz.primehunt.ui.screen.relic.RelicsScreen
import com.felipimatheuz.primehunt.ui.screen.splash.SplashScreen
import com.felipimatheuz.primehunt.ui.viewmodel.AppSettingsViewModel
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
fun MainContent(
    settingsViewModel: AppSettingsViewModel = hiltViewModel()
) {
    val navState = rememberNavigationState(
        startRoute = OverviewKey,
        topLevelRoutes = AppNavKey.topLevelRoutes
    )
    val navigator = remember(navState) { Navigator(navState) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val selectedWallpaper by settingsViewModel.selectedWallpaper.collectAsStateWithLifecycle()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(16.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.cephalon_body),
                        contentDescription = null,
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                DrawerContent(
                    currentKey = (navState.topLevelRoute as? AppNavKey) ?: OverviewKey,
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
                    currentKey = (navState.topLevelRoute as? AppNavKey) ?: OverviewKey,
                    onMenuClick = { scope.launch { drawerState.open() } },
                    selectedWallpaper = selectedWallpaper,
                    onWallpaperSelected = settingsViewModel::updateWallpaper
                )
            }
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize()) {
                if (((navState.topLevelRoute as? AppNavKey) ?: OverviewKey).supportsWallpaper) {
                    PrimeBackgroundPattern(
                        icon = selectedWallpaper,
                        modifier = Modifier.padding(padding)
                    )
                }

                NavDisplay(
                    entries = navState.toEntries { key ->
                        NavEntry(key, metadata = mapOf("route" to key)) {
                            when (val appNavKey = key as AppNavKey) {
                                is OverviewKey -> {
                                    OverviewScreen(padding)
                                }


                                is PrimeSetsKey -> PrimeSetScreen(padding) { set ->
                                    navigator.navigate(PrimeDetailKey(set.id))
                                }

                                is GoalsKey -> {
                                    GoalsScreen(
                                        padding,
                                        onAddGoal = {
                                            navigator.navigate(NewGoalKey())
                                        },
                                        onGoalClick = { goal ->
                                            navigator.navigate(GoalDetailKey(goal.id))
                                        }
                                    )
                                }

                                is NewGoalKey -> {
                                    ManageGoalScreen(
                                        goalId = null,
                                        navigationId = appNavKey.id,
                                        paddingValues = padding,
                                        onBack = { navigator.goBack() }
                                    )
                                }

                                is GoalDetailKey -> {
                                    ManageGoalScreen(
                                        goalId = appNavKey.goalId,
                                        navigationId = appNavKey.goalId.toString(),
                                        paddingValues = padding,
                                        onBack = { navigator.goBack() }
                                    )
                                }

                                is PrimeDetailKey -> {
                                    PrimeDetailScreen(padding, appNavKey.setId) {
                                        navigator.goBack()
                                    }
                                }

                                is RelicsKey -> {
                                    RelicsScreen(padding)
                                }

                                is SyncKey -> {
                                    SyncAccountScreen(padding)
                                }

                                is HelpKey -> {
                                    HelpScreen(padding)
                                }

                                is AboutKey -> {
                                    AboutScreen(padding)
                                }

                            }
                        }
                    },
                    transitionSpec = { calculateTransition() },
                    popTransitionSpec = { calculatePopTransition() },
                    onBack = { navigator.goBack() }
                )
            }
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
            selected = currentKey == appNavKey,
            onClick = { onKeySelected(appNavKey) }
        )
    }
}

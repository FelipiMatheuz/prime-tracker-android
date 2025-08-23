package com.felipimatheuz.primehunt.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.felipimatheuz.primehunt.business.state.BottomNavItem
import com.felipimatheuz.primehunt.business.util.PrimeFilter
import com.felipimatheuz.primehunt.ui.screen.OtherPrimeScreen
import com.felipimatheuz.primehunt.ui.screen.OverviewScreen
import com.felipimatheuz.primehunt.ui.screen.PrimeSetRoute
import com.felipimatheuz.primehunt.ui.screen.RelicScreen

@Composable
fun NavigationGraph(navController: NavHostController, padding: PaddingValues) {
    var updateState by rememberSaveable { mutableStateOf(true) }
    NavHost(navController = navController, startDestination = BottomNavItem.Overview.screenRoute) {
        composable(BottomNavItem.Overview.screenRoute) {
            OverviewScreen(padding, updateState, { updateState = false })
        }
        composable(BottomNavItem.PrimeSets.screenRoute) {
            PrimeSetRoute(padding)
        }
        composable(BottomNavItem.OtherPrimes.screenRoute) {
            it.arguments?.getString("filter")?.let { filter -> PrimeFilter.valueOf(filter) }
                ?.let { primeFilter -> OtherPrimeScreen(padding, primeFilter) }
        }
        composable(BottomNavItem.Relics.screenRoute) {
            it.arguments?.getString("filter")?.let { filter -> PrimeFilter.valueOf(filter) }
                ?.let { primeFilter -> RelicScreen(padding, primeFilter) }
        }
    }
}
package com.felipimatheuz.primehunt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.felipimatheuz.primehunt.business.state.BottomNavItem
import com.felipimatheuz.primehunt.business.util.PrimeFilter
import com.felipimatheuz.primehunt.ui.navigation.BottomNav
import com.felipimatheuz.primehunt.ui.navigation.TopToolbar
import com.felipimatheuz.primehunt.ui.screen.*
import com.felipimatheuz.primehunt.ui.theme.WarframeprimehuntTheme

class PrimeApplication : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WarframeprimehuntTheme {
                var finishSplash by rememberSaveable { mutableStateOf(false) }
                val navController = rememberNavController()
                if (finishSplash) {
                    Scaffold(
                        topBar = {
                            TopToolbar(navController = navController)
                        },
                        bottomBar = {
                            BottomNav(navController = navController)
                        }
                    ) {
                        NavigationGraph(navController = navController, padding = it)
                    }
                } else {
                    SplashScreen { finishSplash = true }
                }
            }
        }
    }

    @Composable
    private fun NavigationGraph(navController: NavHostController, padding: PaddingValues) {
        var updateState by rememberSaveable { mutableStateOf(true) }
        NavHost(navController = navController, startDestination = BottomNavItem.Overview.screenRoute) {
            composable(BottomNavItem.Overview.screenRoute) {
                OverviewScreen(padding, updateState) { updateState = false }
            }
            composable(BottomNavItem.PrimeSets.screenRoute) {
                it.arguments?.getString("filter")?.let { filter -> PrimeFilter.valueOf(filter) }
                    ?.let { primeFilter -> PrimeSetScreen(padding, primeFilter) }
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
}
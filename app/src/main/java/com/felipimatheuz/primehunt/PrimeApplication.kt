package com.felipimatheuz.primehunt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.felipimatheuz.primehunt.state.BottomNavItem
import com.felipimatheuz.primehunt.ui.navigation.BottomNav
import com.felipimatheuz.primehunt.ui.navigation.TopToolbar
import com.felipimatheuz.primehunt.ui.screen.OverviewScreen
import com.felipimatheuz.primehunt.ui.screen.SplashScreen
import com.felipimatheuz.primehunt.ui.theme.WarframeprimehuntTheme

class PrimeApplication : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
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
        NavHost(navController = navController, startDestination = BottomNavItem.Overview.screenRoute) {
            composable(BottomNavItem.Overview.screenRoute) {
                OverviewScreen(padding)
            }
            composable(BottomNavItem.PrimeSets.screenRoute) {
                //PrimeSetScreen()
            }
            composable(BottomNavItem.OtherPrimes.screenRoute) {
                //OtherPrimeScreen()
            }
            composable(BottomNavItem.Relics.screenRoute) {
                //RelicsScreen()
            }
        }
    }
}
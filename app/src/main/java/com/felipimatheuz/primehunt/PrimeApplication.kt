package com.felipimatheuz.primehunt

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.felipimatheuz.primehunt.state.BottomNavItem
import com.felipimatheuz.primehunt.ui.navigation.BottomNav
import com.felipimatheuz.primehunt.ui.navigation.TopToolbar
import com.felipimatheuz.primehunt.ui.screen.SplashScreen
import com.felipimatheuz.primehunt.ui.theme.WarframeprimehuntTheme

class PrimeApplication : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
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
                            TopToolbar()
                        },
                        bottomBar = {
                            BottomNav(navController = navController)
                        }
                    ) {
                        NavigationGraph(navController = navController)
                    }
                } else {
                    SplashScreen { finishSplash = true }
                }
            }
        }
    }

    @Composable
    private fun NavigationGraph(navController: NavHostController) {
        NavHost(navController = navController, startDestination = BottomNavItem.Overview.screenRoute) {
            composable(BottomNavItem.Overview.screenRoute) {
                //OverviewScreen()
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
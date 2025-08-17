package com.felipimatheuz.primehunt.ui.screen

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.compose.rememberNavController
import com.felipimatheuz.primehunt.ui.navigation.BottomNav
import com.felipimatheuz.primehunt.ui.navigation.NavigationGraph
import com.felipimatheuz.primehunt.ui.navigation.TopToolbar

@Composable
fun MainScreen() {
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
        SplashScreen ({ finishSplash = true } )
    }
}
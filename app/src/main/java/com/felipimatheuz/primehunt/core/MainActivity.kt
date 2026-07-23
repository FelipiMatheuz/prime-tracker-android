package com.felipimatheuz.primehunt.core

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.felipimatheuz.primehunt.ui.screen.MainScreen
import com.felipimatheuz.primehunt.ui.theme.PrimeTrackerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PrimeTrackerTheme {
                MainScreen()
            }
        }
    }
}
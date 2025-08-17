package com.felipimatheuz.primehunt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.felipimatheuz.primehunt.ui.screen.MainScreen
import com.felipimatheuz.primehunt.ui.theme.PrimeTrackerTheme

class PrimeApplication : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PrimeTrackerTheme {
                MainScreen()
            }
        }
    }
}
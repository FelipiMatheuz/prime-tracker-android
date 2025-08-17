package com.felipimatheuz.primehunt.core

import android.app.Application
import com.felipimatheuz.primehunt.service.ads.AdManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class PrimeApplication : Application() {
    @Inject
    lateinit var adManager: AdManager

    override fun onCreate() {
        super.onCreate()
        adManager.initialiseUnity()
    }
}
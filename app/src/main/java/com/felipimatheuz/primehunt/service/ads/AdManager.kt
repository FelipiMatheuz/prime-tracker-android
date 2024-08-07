package com.felipimatheuz.primehunt.service.ads

import android.content.Context
import com.felipimatheuz.primehunt.BuildConfig
import com.unity3d.ads.IUnityAdsInitializationListener
import com.unity3d.ads.UnityAds

class AdManager {
    fun initialiseUnity(context: Context) {
        UnityManager().initUnity(context)
    }

    inner class UnityManager : IUnityAdsInitializationListener {
        fun initUnity(context: Context) {
            if (!UnityAds.isInitialized) {
                var testMode = false
                if (BuildConfig.DEBUG) {
                    UnityAds.debugMode = true
                    testMode = true
                }
                UnityAds.initialize(context, "5401575", testMode, this)
            }
        }

        override fun onInitializationComplete() {
        }

        override fun onInitializationFailed(p0: UnityAds.UnityAdsInitializationError?, p1: String?) {
        }
    }
}
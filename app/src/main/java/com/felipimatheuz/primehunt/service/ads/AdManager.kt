package com.felipimatheuz.primehunt.service.ads

import android.content.Context
import com.felipimatheuz.primehunt.BuildConfig
import com.unity3d.ads.IUnityAdsInitializationListener
import com.unity3d.ads.UnityAds
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdManager @Inject constructor(
    @param:ApplicationContext private val context: Context
) {

    fun initialiseUnity() {
        UnityManager().initUnity()
    }

    inner class UnityManager : IUnityAdsInitializationListener {
        fun initUnity() {
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
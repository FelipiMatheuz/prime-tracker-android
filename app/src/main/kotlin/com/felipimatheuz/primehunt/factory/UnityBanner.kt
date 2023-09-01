package com.felipimatheuz.primehunt.factory

import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import com.unity3d.ads.IUnityAdsInitializationListener
import com.unity3d.ads.UnityAds
import com.unity3d.ads.UnityAds.UnityAdsInitializationError
import com.unity3d.services.banners.BannerErrorInfo
import com.unity3d.services.banners.BannerView
import com.unity3d.services.banners.BannerView.IListener
import com.unity3d.services.banners.UnityBannerSize


class UnityBanner : IUnityAdsInitializationListener {
    private var unityGameID = "5401575"
    private var testMode = false
    private var adUnitId = "Banner_Android"

    private val bannerListener: IListener = object : IListener {
        override fun onBannerLoaded(bannerAdView: BannerView) {
        }

        override fun onBannerFailedToLoad(bannerAdView: BannerView, errorInfo: BannerErrorInfo) {
        }

        override fun onBannerClick(bannerAdView: BannerView) {
        }

        override fun onBannerLeftApplication(bannerAdView: BannerView) {
        }
    }

    fun loadBannerAd(bannerLayout: RelativeLayout, activity: AppCompatActivity) {
        UnityAds.initialize(activity, unityGameID, testMode, this)
        val banner = BannerView(activity, adUnitId, UnityBannerSize.getDynamicSize(activity))
        banner.listener = bannerListener
        banner.load()
        bannerLayout.addView(banner)
    }

    override fun onInitializationComplete() {
    }

    override fun onInitializationFailed(error: UnityAdsInitializationError, message: String) {}

    companion object {
        fun getBanner(bannerLayout: RelativeLayout, activity: AppCompatActivity) {
            return UnityBanner().loadBannerAd(bannerLayout, activity)
        }
    }
}
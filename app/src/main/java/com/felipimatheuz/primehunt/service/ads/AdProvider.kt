package com.felipimatheuz.primehunt.service.ads

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import com.felipimatheuz.primehunt.business.util.dpFromPx
import com.unity3d.services.banners.BannerErrorInfo
import com.unity3d.services.banners.BannerView
import com.unity3d.services.banners.UnityBannerSize


abstract class AdsProvider : IAdsProvider {
    fun setupUnity(
        context: Context,
        bannerId: String
    ): BannerView {
        return BannerView(context as Activity?, bannerId, UnityBannerSize(320, 50)).apply {
            try {
                listener = object : BannerView.IListener {
                    override fun onBannerLoaded(bannerAdView: BannerView?) {
                        Handler(Looper.getMainLooper()).postDelayed({
                            onAdLoaded(dpFromPx(context, height))
                        }, 500)
                    }

                    override fun onBannerShown(bannerAdView: BannerView?) {
                        //Banner Showed
                    }

                    override fun onBannerClick(bannerAdView: BannerView?) {
                        onAdClicked()
                    }

                    override fun onBannerFailedToLoad(bannerAdView: BannerView?, errorInfo: BannerErrorInfo?) {
                        onAdFailedToLoad()
                    }

                    override fun onBannerLeftApplication(bannerView: BannerView?) {
                        onAdOpened()
                    }
                }
                load()
            } catch (_: Exception) {
            }
        }
    }
}
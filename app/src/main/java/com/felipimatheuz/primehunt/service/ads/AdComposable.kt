package com.felipimatheuz.primehunt.service.ads

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun BannerAdView(
    bannerId: String,
    modifier: Modifier
) {
    val configuration = LocalConfiguration
    configuration.current.screenHeightDp
    configuration.current.orientation
    val showBanner = remember { mutableStateOf(true) }
    var bannerAdLoaded by remember { mutableStateOf(false) }
    if (showBanner.value) {
        Box(
            modifier = modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            AndroidView(
                modifier = Modifier.fillMaxWidth().height(
                    if (bannerAdLoaded) {
                        58.dp
                    } else {
                        0.dp
                    }
                ).align(Alignment.TopCenter),
                factory = { context ->
                    val adsManager = object : AdsProvider() {
                        override fun onAdLoaded(height: Dp) {
                            bannerAdLoaded = true
                        }

                        override fun onAdFailedToLoad() {
                            bannerAdLoaded = false
                        }

                        override fun onAdOpened() {
                            showBanner.value = false
                        }

                        override fun onAdImpression() {}
                        override fun onAdClicked() {}
                        override fun onAdClosed() {}
                    }
                    adsManager.setupUnity(
                        context = context,
                        bannerId = bannerId
                    )
                }
            )
        }
    }
}
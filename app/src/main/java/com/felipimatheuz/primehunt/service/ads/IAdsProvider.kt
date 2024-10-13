package com.felipimatheuz.primehunt.service.ads

import androidx.compose.ui.unit.Dp

interface IAdsProvider {
    fun onAdLoaded(height: Dp)
    fun onAdImpression()
    fun onAdFailedToLoad()
    fun onAdOpened()
    fun onAdClicked()
    fun onAdClosed()

}
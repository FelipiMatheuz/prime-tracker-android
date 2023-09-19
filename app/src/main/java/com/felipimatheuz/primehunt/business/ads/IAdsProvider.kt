package com.felipimatheuz.primehunt.business.ads

import androidx.compose.ui.unit.Dp

interface IAdsProvider {
    fun onAdLoaded(height: Dp)
    fun onAdImpression()
    fun onAdFailedToLoad()
    fun onAdOpened()
    fun onAdClicked()
    fun onAdClosed()

}
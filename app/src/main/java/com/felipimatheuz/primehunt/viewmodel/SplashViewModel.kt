package com.felipimatheuz.primehunt.viewmodel

import androidx.lifecycle.ViewModel
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.business.state.LoadState
import com.felipimatheuz.primehunt.service.api.ApiService
import kotlinx.coroutines.flow.MutableStateFlow

class SplashViewModel : ViewModel() {
    val loadState = MutableStateFlow<LoadState>(LoadState.LoadRelic)

    fun loadResource(textRes: Int) {
        try {
            when (textRes) {
                R.string.loading_content -> {
                    ApiService().getPrimeSetList(textRes, loadState)
                }

                R.string.checking_set_updates -> {
                    ApiService().getRelicSetList(textRes, loadState)
                }

                else -> {
                    ApiService().getOtherPrimeList(textRes, loadState)
                }
            }
        } catch (e: Exception) {
            loadState.value = LoadState.Error(textRes, e.message)
        }
    }
}
package com.felipimatheuz.primehunt.viewmodel

import androidx.lifecycle.ViewModel
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.model.resources.PrimeRelicApi
import com.felipimatheuz.primehunt.model.resources.PrimeSetApi
import com.felipimatheuz.primehunt.state.LoadState
import com.felipimatheuz.primehunt.util.apiData
import com.felipimatheuz.primehunt.util.apiRelic
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow

class SplashViewModel : ViewModel() {
    val loadState = MutableStateFlow<LoadState>(LoadState.LoadRelic)

    @OptIn(DelicateCoroutinesApi::class)
    fun loadResource(textRes: Int) {
        try {
            if (textRes == R.string.loading_content) {
                GlobalScope.launch {
                    apiRelic = PrimeRelicApi.singleInstance()
                    loadState.value = LoadState.LoadSet
                }
            } else {
                GlobalScope.launch {
                    apiData = PrimeSetApi.singleInstance()
                    loadState.value = LoadState.Ready
                }
            }
        } catch (e: Exception) {
            loadState.value = LoadState.Error(textRes)
        }
    }
}
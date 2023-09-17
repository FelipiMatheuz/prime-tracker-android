package com.felipimatheuz.primehunt.viewmodel

import androidx.lifecycle.ViewModel
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.business.external.OtherPrimeApi
import com.felipimatheuz.primehunt.business.external.PrimeRelicApi
import com.felipimatheuz.primehunt.business.external.PrimeSetApi
import com.felipimatheuz.primehunt.business.state.LoadState
import com.felipimatheuz.primehunt.business.util.apiOther
import com.felipimatheuz.primehunt.business.util.apiSet
import com.felipimatheuz.primehunt.business.util.apiRelic
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow

class SplashViewModel : ViewModel() {
    val loadState = MutableStateFlow<LoadState>(LoadState.LoadRelic)

    @OptIn(DelicateCoroutinesApi::class)
    fun loadResource(textRes: Int) {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                when (textRes) {
                    R.string.loading_content -> {
                        GlobalScope.async {
                            apiRelic = PrimeRelicApi.singleInstance()
                            loadState.value = LoadState.LoadSet
                        }.await()
                    }
                    R.string.checking_set_updates -> {
                        GlobalScope.async {
                            apiSet = PrimeSetApi.singleInstance()
                            loadState.value = LoadState.LoadOther
                        }.await()
                    }
                    else -> {
                        GlobalScope.async {
                            apiOther = OtherPrimeApi.singleInstance()
                            loadState.value = LoadState.Ready
                        }.await()
                    }
                }
            } catch (e: Exception) {
                loadState.value = LoadState.Error(textRes)
            }
        }
    }
}
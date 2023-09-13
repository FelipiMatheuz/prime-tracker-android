package com.felipimatheuz.primehunt.viewmodel

import androidx.lifecycle.ViewModel
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.model.external.OtherPrimeApi
import com.felipimatheuz.primehunt.model.external.PrimeRelicApi
import com.felipimatheuz.primehunt.model.external.PrimeSetApi
import com.felipimatheuz.primehunt.state.LoadState
import com.felipimatheuz.primehunt.util.apiOther
import com.felipimatheuz.primehunt.util.apiSet
import com.felipimatheuz.primehunt.util.apiRelic
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
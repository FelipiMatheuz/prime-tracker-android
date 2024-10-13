package com.felipimatheuz.primehunt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felipimatheuz.primehunt.business.state.LoadState
import com.felipimatheuz.primehunt.business.util.otherPrimeList
import com.felipimatheuz.primehunt.business.util.primeSetList
import com.felipimatheuz.primehunt.business.util.relicList
import com.felipimatheuz.primehunt.service.api.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {
    val loadState = MutableStateFlow<LoadState>(LoadState.LoadRelic)

    fun loadResource(previousLoadState: LoadState = loadState.value) {
        viewModelScope.launch {
            try {
                when (previousLoadState) {
                    LoadState.LoadRelic -> {
                        relicList = ApiService.getRelicSetList()
                        relicList.forEach { relicSet ->
                            relicSet.name = relicSet.name.removeSuffix(" Intact")
                        }
                        loadState.value = LoadState.LoadSet
                    }

                    LoadState.LoadSet -> {
                        primeSetList = ApiService.getPrimeSetList()
                        loadState.value = LoadState.LoadOther
                    }

                    LoadState.LoadOther -> {
                        otherPrimeList = ApiService.getOtherPrimeList()
                        loadState.value = LoadState.Ready
                    }

                    else -> {} //Nothing
                }
            } catch (e: Exception) {
                val previous = loadState.value
                loadState.value = LoadState.Error(previous, e.message)
            }
        }
    }
}
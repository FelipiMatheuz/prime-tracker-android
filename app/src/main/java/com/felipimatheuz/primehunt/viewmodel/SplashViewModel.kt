package com.felipimatheuz.primehunt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felipimatheuz.primehunt.business.state.LoadState
import com.felipimatheuz.primehunt.business.util.otherPrimeList
import com.felipimatheuz.primehunt.business.util.primeSetList
import com.felipimatheuz.primehunt.business.util.relicList
import com.felipimatheuz.primehunt.service.api.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(private val apiService: ApiService) : ViewModel() {
    val loadState = MutableStateFlow<LoadState>(LoadState.LoadRelic)

    fun loadResource(previousLoadState: LoadState = loadState.value) {
        viewModelScope.launch {
            try {
                when (previousLoadState) {
                    LoadState.LoadRelic -> {
                        relicList = apiService.getRelicSetList()
                        relicList.forEach { relicSet ->
                            relicSet.name = relicSet.name.removeSuffix(" Intact")
                        }
                        loadState.value = LoadState.LoadSet
                    }

                    LoadState.LoadSet -> {
                        primeSetList = apiService.getPrimeSetList()
                        loadState.value = LoadState.LoadOther
                    }

                    LoadState.LoadOther -> {
                        otherPrimeList = apiService.getOtherPrimeList()
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
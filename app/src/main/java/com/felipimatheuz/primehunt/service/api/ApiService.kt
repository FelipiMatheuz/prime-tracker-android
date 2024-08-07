package com.felipimatheuz.primehunt.service.api

import com.felipimatheuz.primehunt.business.state.LoadState
import com.felipimatheuz.primehunt.business.util.otherPrimeList
import com.felipimatheuz.primehunt.business.util.primeSetList
import com.felipimatheuz.primehunt.business.util.relicList
import com.felipimatheuz.primehunt.model.PrimeItem
import com.felipimatheuz.primehunt.model.PrimeSet
import com.felipimatheuz.primehunt.model.RelicSet
import kotlinx.coroutines.flow.MutableStateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ApiService {

    fun getPrimeSetList(textRes: Int, loadState: MutableStateFlow<LoadState>) {
        RetrofitConfig().trackerService().primeSets().enqueue(object : Callback<List<PrimeSet>?> {
            override fun onResponse(call: Call<List<PrimeSet>?>, response: Response<List<PrimeSet>?>) {
                response.body()?.let {
                    primeSetList = it
                    loadState.value = LoadState.LoadSet
                }
            }

            override fun onFailure(call: Call<List<PrimeSet>?>, t: Throwable) {
                primeSetList = listOf()
                loadState.value = LoadState.Error(textRes, t.message)
            }
        })
    }

    fun getOtherPrimeList(textRes: Int, loadState: MutableStateFlow<LoadState>) {
        RetrofitConfig().trackerService().otherPrimes().enqueue(object : Callback<List<PrimeItem>?> {
            override fun onResponse(call: Call<List<PrimeItem>?>, response: Response<List<PrimeItem>?>) {
                response.body()?.let {
                    otherPrimeList = it
                    loadState.value = LoadState.Ready
                }
            }

            override fun onFailure(call: Call<List<PrimeItem>?>, t: Throwable) {
                otherPrimeList = listOf()
                loadState.value = LoadState.Error(textRes, t.message)
            }
        })
    }

    fun getRelicSetList(textRes: Int, loadState: MutableStateFlow<LoadState>) {
        RetrofitConfig().trackerService().relics().enqueue(object : Callback<List<RelicSet>?> {
            override fun onResponse(call: Call<List<RelicSet>?>, response: Response<List<RelicSet>?>) {
                response.body()?.let {
                    relicList = it.filter { relicSet -> relicSet.name.contains("Intact") && relicSet.rewards.isNotEmpty() }
                    relicList.forEach { relicSet ->
                        relicSet.name = relicSet.name.removeSuffix(" Intact")
                    }
                    loadState.value = LoadState.LoadOther
                }
            }

            override fun onFailure(call: Call<List<RelicSet>?>, t: Throwable) {
                relicList = listOf()
                loadState.value = LoadState.Error(textRes, t.message)
            }
        })
    }
}
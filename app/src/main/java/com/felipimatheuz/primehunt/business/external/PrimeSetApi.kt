package com.felipimatheuz.primehunt.business.external

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.felipimatheuz.primehunt.model.PrimeSet
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.net.URL


class PrimeSetApi {
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
    private var setData: List<PrimeSet> = listOf()

    suspend fun setSetData() {
        return coroutineScope {
            val resultData = async(defaultDispatcher) { loadData() }
            setData = resultData.await()
        }
    }

    fun getSetData(): List<PrimeSet> = setData

    private fun loadData(): List<PrimeSet> {
        val mapper = jacksonObjectMapper()
        val origin = "https://raw.githubusercontent.com"
        val path = "/FelipiMatheuz/WPH/main/api/prime_sets.json"
        val url = URL("$origin$path")
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        val result = mutableListOf<PrimeSet>()
        for (primeSetJson in mapper.readValue(url, List::class.java)) {
            result.add(mapper.convertValue(primeSetJson, PrimeSet::class.java))
        }
        return result
    }

    companion object {
        suspend fun singleInstance(): PrimeSetApi {
            val primeSetApi = PrimeSetApi()
            primeSetApi.setSetData()
            return primeSetApi
        }
    }
}
package com.felipimatheuz.primehunt.service.api

import com.felipimatheuz.primehunt.model.PrimeItem
import com.felipimatheuz.primehunt.model.PrimeSet
import com.felipimatheuz.primehunt.model.RelicSet
import retrofit2.Call
import retrofit2.http.GET

interface PrimeTrackerService {
    @GET("/FelipiMatheuz/WPH/main/api/prime_sets.json")
    fun primeSets() : Call<List<PrimeSet>>

    @GET("/FelipiMatheuz/WPH/main/api/other_primes.json")
    fun otherPrimes() : Call<List<PrimeItem>>

    @GET("/WFCD/warframe-items/master/data/json/Relics.json")
    fun relics() : Call<List<RelicSet>>
}
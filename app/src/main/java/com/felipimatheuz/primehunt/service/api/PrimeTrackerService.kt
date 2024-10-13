package com.felipimatheuz.primehunt.service.api

import com.felipimatheuz.primehunt.model.PrimeItem
import com.felipimatheuz.primehunt.model.PrimeSet
import com.felipimatheuz.primehunt.model.RelicSet
import retrofit2.http.GET

interface PrimeTrackerService {
    @GET("/FelipiMatheuz/WPH/main/api/prime_sets.json")
    suspend fun primeSets() : List<PrimeSet>

    @GET("/FelipiMatheuz/WPH/main/api/other_primes.json")
    suspend fun otherPrimes() : List<PrimeItem>

    @GET("/WFCD/warframe-items/master/data/json/Relics.json")
    suspend fun relics() : List<RelicSet>
}
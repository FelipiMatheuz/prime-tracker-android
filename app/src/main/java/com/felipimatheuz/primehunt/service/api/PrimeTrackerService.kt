package com.felipimatheuz.primehunt.service.api

import com.felipimatheuz.primehunt.data.remote.dto.*
import com.felipimatheuz.primehunt.model.PrimeItem
import retrofit2.http.GET

interface PrimeTrackerService {

    @GET("manifest.json")
    suspend fun readManifest(): RemoteManifest

    @GET("prime-collections.json")
    suspend fun getPrimeCollections(): List<PrimeCollectionDto>

    @GET("prime-sets.json")
    suspend fun getPrimeSets(): List<PrimeSetDto>

    @GET("relics.json")
    suspend fun getRelics(): List<RelicDto>

    @GET("other_primes.json")
    suspend fun otherPrimes(): List<PrimeItem>
}

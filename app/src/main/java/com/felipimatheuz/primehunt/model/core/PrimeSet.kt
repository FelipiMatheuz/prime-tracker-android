package com.felipimatheuz.primehunt.model.core

import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonProperty

@Keep
data class PrimeSet(
    @JsonProperty("imgLink")
    var imgLink: String,
    @JsonProperty("warframe")
    var warframe: PrimeItem,
    @JsonProperty("primeItem1")
    var primeItem1: PrimeItem,
    @JsonProperty("primeItem2")
    var primeItem2: PrimeItem?,
    @JsonProperty("status")
    var status: PrimeStatus = PrimeStatus.VAULT,
    @JsonProperty("released")
    val released: Int
)

@Keep
enum class PrimeStatus {
    ACTIVE,
    VAULT,
    RESURGENCE,
    BARO
}

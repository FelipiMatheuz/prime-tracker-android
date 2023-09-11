package com.felipimatheuz.primehunt.model.core

import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonProperty

@Keep
data class PrimeSet(
    @JsonProperty("imgLink")
    var imgLink: String,
    @JsonProperty("setName")
    var setName: String,
    @JsonProperty("primeItems")
    var primeItems: List<PrimeItem>,
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

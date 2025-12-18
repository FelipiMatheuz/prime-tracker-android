package com.felipimatheuz.primehunt.model

import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonProperty

@Keep
data class PrimeSet(
    @param:JsonProperty("imgLink")
    var imgLink: String = "",
    @param:JsonProperty("setName")
    var setName: String,
    @param:JsonProperty("primeItems")
    var primeItems: List<PrimeItem>,
    @param:JsonProperty("status")
    var status: PrimeStatus = PrimeStatus.VAULT,
    @param:JsonProperty("released")
    val released: Int
)

@Keep
enum class PrimeStatus {
    ACTIVE,
    VAULT,
    RESURGENCE,
    BARO
}

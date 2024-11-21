package com.felipimatheuz.primehunt.model

import androidx.annotation.DrawableRes
import androidx.annotation.Keep
import androidx.annotation.StringRes
import com.fasterxml.jackson.annotation.JsonProperty
import com.felipimatheuz.primehunt.R

@Keep
data class PrimeItem(
    @JsonProperty("name")
    var name: String,
    @JsonProperty("type")
    var type: PrimeType,
    @JsonProperty("components")
    val components: List<ItemComponent>,
    @JsonProperty("blueprint")
    var blueprint: Boolean = false
)

@Keep
data class ItemComponent(
    @JsonProperty("part")
    val part: ItemPart,
    @JsonProperty("obtained")
    var obtained: Boolean = false
)

@Keep
enum class PrimeType {
    WARFRAME,
    PRIMARY,
    SECONDARY,
    MELEE,
    OTHER
}

@Keep
enum class ItemPart(@StringRes val text: Int, @DrawableRes val icon: Int) {
    NEUROPTICS(R.string.comp_neuroptics, R.drawable.prime_neuroptics),
    CHASSIS(R.string.comp_chassis, R.drawable.prime_chassis),
    SYSTEMS(R.string.comp_systems, R.drawable.prime_systems),
    BARREL(R.string.comp_barrel, R.drawable.prime_barrel),
    RECEIVER(R.string.comp_receiver, R.drawable.prime_receiver),
    STOCK(R.string.comp_stock, R.drawable.prime_stock),
    LINK(R.string.comp_link, R.drawable.prime_link),
    CARAPACE(R.string.comp_carapace, R.drawable.prime_chassis),
    CEREBRUM(R.string.comp_cerebrum, R.drawable.prime_neuroptics),
    BLADE(R.string.comp_blade, R.drawable.prime_blade),
    DISC(R.string.comp_disc, R.drawable.prime_blade),
    ORNAMENT(R.string.comp_ornament, R.drawable.prime_link),
    HANDLE(R.string.comp_handle, R.drawable.prime_handle),
    GAUNTLET(R.string.comp_gauntlet, R.drawable.prime_handle),
    HILT(R.string.comp_hilt, R.drawable.prime_handle),
    POUCH(R.string.comp_pouch, R.drawable.prime_grip),
    STARS(R.string.comp_stars, R.drawable.prime_blade),
    HARNESS(R.string.comp_harness, R.drawable.prime_harness),
    CIRCUIT(R.string.comp_systems, R.drawable.prime_circuit), // archwing systems
    WINGS(R.string.comp_wings, R.drawable.prime_wings),
    BAND(R.string.comp_band, R.drawable.prime_grip),
    BUCKLE(R.string.comp_buckle, R.drawable.prime_link),
    HEAD(R.string.comp_head, R.drawable.prime_blade),
    ULIMB(R.string.comp_ulimb, R.drawable.prime_blade),
    LLIMB(R.string.comp_llinb, R.drawable.prime_blade),
    GRIP(R.string.comp_grip, R.drawable.prime_grip),
    STRING(R.string.comp_string, R.drawable.prime_stock),
    GUARD(R.string.comp_guard, R.drawable.prime_guard),
    BOOT(R.string.comp_boot, R.drawable.prime_guard),
    CHAIN(R.string.comp_chain, R.drawable.prime_stock),

    //special parts:
    BRONCO(R.string.bronco_prime, R.drawable.ic_weapon),
    LEX(R.string.lex_prime, R.drawable.ic_weapon),
    MAGNUS(R.string.magnus_prime, R.drawable.ic_weapon),
    VASTO(R.string.vasto_prime, R.drawable.ic_weapon)
}

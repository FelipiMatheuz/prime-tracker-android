package com.felipimatheuz.primehunt.domain.model

import com.felipimatheuz.primehunt.data.remote.enums.DropRarity
import com.felipimatheuz.primehunt.data.remote.enums.PrimePartType
import com.felipimatheuz.primehunt.data.remote.enums.PrimeType
import com.felipimatheuz.primehunt.data.remote.enums.RelicSource

data class PrimeCollection(
    val id: String,
    val name: String,
    val promoImage: String,
    val sets: List<PrimeSetDomain>
)

data class PrimeSetDomain(
    val id: String,
    val name: String,
    val type: PrimeType,
    val imageUrl: String,
    val parts: List<PrimePartDomain> = emptyList()
) {
    val totalPieces: Int get() = parts.sumOf { it.totalNeeded }
    val ownedPieces: Int get() = parts.sumOf { it.totalOwned }
    val availability: RelicSource get() = parts
        .filter { it.name != PrimePartType.PRIME_SET }
        .map { it.bestSource }
        .minByOrNull { it.ordinal } ?: RelicSource.VAULT
    val isNested: Boolean get() = parts.any { it.name == PrimePartType.PRIME_SET }
}

data class PrimePartDomain(
    val id: String,
    val name: PrimePartType,
    val neededQuantity: Int,
    val ownedQuantity: Int,
    val relics: List<RelicRewardDomain> = emptyList(),
    val bestSource: RelicSource = RelicSource.VAULT,
    val imageUrl: String? = null,
    val nestedParts: List<PrimePartDomain> = emptyList()
) {
    val totalNeeded: Int get() = if (nestedParts.isEmpty()) neededQuantity else nestedParts.sumOf { it.totalNeeded }
    val totalOwned: Int get() = if (nestedParts.isEmpty()) minOf(ownedQuantity, neededQuantity) else nestedParts.sumOf { it.totalOwned }
}

data class RelicRewardDomain(
    val name: String,
    val rarity: DropRarity,
    val source: RelicSource
)

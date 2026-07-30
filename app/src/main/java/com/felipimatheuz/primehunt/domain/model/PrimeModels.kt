package com.felipimatheuz.primehunt.domain.model

import com.felipimatheuz.primehunt.data.remote.enums.DropRarity
import com.felipimatheuz.primehunt.data.remote.enums.PrimePartType
import com.felipimatheuz.primehunt.data.remote.enums.PrimeType
import com.felipimatheuz.primehunt.data.remote.enums.RelicEra
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
    
    val availableRelics: List<RelicRewardDomain> get() = relics.filter { it.source != RelicSource.VAULT }
    val vaultedRelics: List<RelicRewardDomain> get() = relics.filter { it.source == RelicSource.VAULT }
}

data class RelicRewardDomain(
    val name: String,
    val rarity: DropRarity,
    val source: RelicSource
)

data class RelicDomain(
    val id: String,
    val name: String,
    val era: RelicEra,
    val source: RelicSource,
    val rewards: List<RelicComponentDomain>
) {
    val missingCount: Int get() = rewards.count { !it.isObtained && !it.isForma }
    val hasForma: Boolean get() = rewards.any { it.isForma }
    val isCompleted: Boolean get() = missingCount == 0
    val goalCount: Int get() = rewards.count { it.goalTags.isNotEmpty() }
}

data class RelicComponentDomain(
    val name: String,
    val rarity: DropRarity,
    val isObtained: Boolean,
    val neededQuantity: Int = 0,
    val ownedQuantity: Int = 0,
    val compositeInfo: String? = null,
    val goalTags: List<Int> = emptyList(),
    val isForma: Boolean = false,
    val nameSuffixRes: Int? = null
)

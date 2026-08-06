package com.felipimatheuz.primehunt.domain.model

import androidx.compose.runtime.Immutable
import com.felipimatheuz.primehunt.data.local.enums.GoalIcons
import com.felipimatheuz.primehunt.data.remote.enums.DropRarity
import com.felipimatheuz.primehunt.data.remote.enums.PrimePartType
import com.felipimatheuz.primehunt.data.remote.enums.PrimeType
import com.felipimatheuz.primehunt.data.remote.enums.RelicEra
import com.felipimatheuz.primehunt.data.remote.enums.RelicSource

@Immutable
data class GoalTagDomain(
    val id: Long,
    val name: String,
    val icon: GoalIcons,
    val color: Int
)

@Immutable
data class GoalDomain(
    val id: Long,
    val targetId: String,
    val targetName: String,
    val targetType: com.felipimatheuz.primehunt.data.local.enums.GoalTargetType,
    val currentQuantity: Int,
    val desiredQuantity: Int,
    val status: com.felipimatheuz.primehunt.data.local.enums.GoalStatus,
    val note: String?,
    val tag: GoalTagDomain
)

@Immutable
data class TargetDomain(
    val id: String,
    val name: String,
    val type: com.felipimatheuz.primehunt.data.local.enums.GoalTargetType
)

@Immutable
data class PrimeCollection(
    val id: String,
    val name: String,
    val promoImage: String,
    val sets: List<PrimeSetDomain>
)

@Immutable
data class PrimeSetDomain(
    val id: String,
    val name: String,
    val type: PrimeType,
    val imageUrl: String,
    val parts: List<PrimePartDomain> = emptyList()
) : ProgressState {
    val totalPieces: Int get() = parts.sumOf { it.totalNeeded }
    val ownedPieces: Int get() = parts.sumOf { it.totalOwned }

    override val progressTotal: Int get() = totalPieces
    override val progressOwned: Int get() = ownedPieces

    val availability: RelicSource get() = parts
        .filter { it.name != PrimePartType.PRIME_SET }
        .map { it.bestSource }
        .minByOrNull { it.ordinal } ?: RelicSource.VAULT
    val isNested: Boolean get() = parts.any { it.name == PrimePartType.PRIME_SET }
}

@Immutable
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

@Immutable
data class RelicRewardDomain(
    val name: String,
    val rarity: DropRarity,
    val source: RelicSource
)

@Immutable
data class RelicDomain(
    val id: String,
    val name: String,
    val era: RelicEra,
    val source: RelicSource,
    val rewards: List<RelicComponentDomain>,
    val goalTags: List<GoalTagDomain> = emptyList()
) : ProgressState {
    val missingCount: Int get() = rewards.count { !it.isObtained && !it.isForma }
    val hasForma: Boolean get() = rewards.any { it.isForma }
    val isCompleted: Boolean get() = missingCount == 0

    override val progressTotal: Int get() = rewards.count { !it.isForma }
    override val progressOwned: Int get() = progressTotal - missingCount

    val goalCount: Int get() = (goalTags + rewards.flatMap { it.goalTags }).distinctBy { it.id }.size
}

@Immutable
data class RelicComponentDomain(
    val name: String,
    val rarity: DropRarity,
    val isObtained: Boolean,
    val neededQuantity: Int = 0,
    val ownedQuantity: Int = 0,
    val compositeInfo: String? = null,
    val goalTags: List<GoalTagDomain> = emptyList(),
    val isForma: Boolean = false,
    val isBlueprint: Boolean = false
)

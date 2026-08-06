package com.felipimatheuz.primehunt.domain.mapper

import com.felipimatheuz.primehunt.data.local.entity.PrimePartEntity
import com.felipimatheuz.primehunt.data.remote.enums.PrimePartType
import com.felipimatheuz.primehunt.data.remote.enums.RelicSource
import com.felipimatheuz.primehunt.data.repository.PrimeBaseData
import com.felipimatheuz.primehunt.domain.model.PrimePartDomain
import com.felipimatheuz.primehunt.domain.model.PrimeSetDomain
import com.felipimatheuz.primehunt.domain.model.RelicRewardDomain

object PrimeMapper {
    
    fun capitalizeWords(text: String): String =
        text.split(" ").joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }

    fun formatPartName(setName: String, partType: PrimePartType): String {
        return "$setName ${capitalizeWords(partType.name.replace("_", " ").lowercase())}"
    }

    fun getBlueprintName(setName: String): String {
        return "$setName Blueprint"
    }

    fun mapToDomainSets(
        data: PrimeBaseData,
        inventoryMap: Map<String, Int>
    ): List<PrimeSetDomain> {
        val relicMap = data.relics.associateBy { it.id }
        val componentMap = data.components.groupBy { it.primePartId }
        val partsBySetMap = data.parts.groupBy { it.primeSetId }
        val setEntityMap = data.sets.associateBy { it.id }

        fun resolveParts(
            setId: String,
            multiplier: Int,
            mapper: (PrimePartEntity, Int) -> PrimePartDomain
        ): List<PrimePartDomain> {
            val setParts = partsBySetMap[setId] ?: emptyList()
            val hasBlueprint = setParts.any { it.id == setId }

            return if (!hasBlueprint) {
                val comps = componentMap[setId] ?: emptyList()
                if (comps.isNotEmpty()) {
                    val blueprint = mapper(
                        PrimePartEntity(setId, setId, PrimePartType.BLUEPRINT, 1),
                        multiplier
                    )
                    val others = setParts.map { mapper(it, multiplier) }
                    listOf(blueprint) + others
                } else {
                    setParts.map { mapper(it, multiplier) }
                }
            } else {
                setParts.map { mapper(it, multiplier) }
            }
        }

        fun mapPart(part: PrimePartEntity, multiplier: Int): PrimePartDomain {
            val comps = componentMap[part.id] ?: emptyList()
            val relicRewards = comps.map { c ->
                val relic = relicMap[c.relicId]
                val formattedName = if (relic != null) "${
                    capitalizeWords(relic.era.name.lowercase())
                } ${relic.name}" else ""
                RelicRewardDomain(formattedName, c.rarity, relic?.source ?: RelicSource.VAULT)
            }
            val sources = comps.mapNotNull { relicMap[it.relicId]?.source }
            val bestSource = sources.minByOrNull { it.ordinal } ?: RelicSource.VAULT

            val nested = if (part.part == PrimePartType.PRIME_SET) {
                resolveParts(part.id, multiplier * part.quantity, ::mapPart)
            } else {
                emptyList()
            }

            val imageUrl = if (part.part == PrimePartType.PRIME_SET) {
                setEntityMap[part.id]?.image
            } else null

            return PrimePartDomain(
                id = part.id,
                name = part.part,
                neededQuantity = part.quantity * multiplier,
                ownedQuantity = inventoryMap[part.id] ?: 0,
                relics = relicRewards,
                bestSource = bestSource,
                imageUrl = imageUrl,
                nestedParts = nested
            )
        }

        return data.sets.map { set ->
            PrimeSetDomain(
                id = set.id,
                name = set.name,
                type = set.type,
                imageUrl = set.image,
                parts = resolveParts(set.id, 1, ::mapPart).sortedBy { it.name }
            )
        }
    }
}

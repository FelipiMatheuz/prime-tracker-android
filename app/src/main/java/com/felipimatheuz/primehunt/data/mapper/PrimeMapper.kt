package com.felipimatheuz.primehunt.data.mapper

import com.felipimatheuz.primehunt.data.local.entity.PrimePartEntity
import com.felipimatheuz.primehunt.data.local.entity.PrimeSetEntity
import com.felipimatheuz.primehunt.data.local.entity.RelicEntity
import com.felipimatheuz.primehunt.data.local.entity.PrimeComponentEntity
import com.felipimatheuz.primehunt.data.model.PrimeBaseData
import com.felipimatheuz.primehunt.domain.model.enums.PrimePartType
import com.felipimatheuz.primehunt.domain.model.enums.RelicSource
import com.felipimatheuz.primehunt.domain.model.PrimePartDomain
import com.felipimatheuz.primehunt.domain.model.PrimeSetDomain
import com.felipimatheuz.primehunt.domain.model.RelicRewardDomain
import com.felipimatheuz.primehunt.domain.util.StringFormatter

object PrimeMapper {

    fun mapToDomainSets(
        data: PrimeBaseData,
        inventoryMap: Map<String, Int>
    ): List<PrimeSetDomain> {
        val relicMap = data.relics.associateBy { it.id }
        val componentMap = data.components.groupBy { it.primePartId }
        val partsBySetMap = data.parts.groupBy { it.primeSetId }
        val setEntityMap = data.sets.associateBy { it.id }

        return data.sets.map { set ->
            mapToDomainSetInternal(
                set.id,
                inventoryMap,
                relicMap,
                componentMap,
                partsBySetMap,
                setEntityMap
            )!!
        }
    }

    fun mapToDomainSet(
        setId: String,
        data: PrimeBaseData,
        inventoryMap: Map<String, Int>
    ): PrimeSetDomain? {
        val relicMap = data.relics.associateBy { it.id }
        val componentMap = data.components.groupBy { it.primePartId }
        val partsBySetMap = data.parts.groupBy { it.primeSetId }
        val setEntityMap = data.sets.associateBy { it.id }

        return mapToDomainSetInternal(
            setId,
            inventoryMap,
            relicMap,
            componentMap,
            partsBySetMap,
            setEntityMap
        )
    }

    private fun mapToDomainSetInternal(
        setId: String,
        inventoryMap: Map<String, Int>,
        relicMap: Map<String, RelicEntity>,
        componentMap: Map<String, List<PrimeComponentEntity>>,
        partsBySetMap: Map<String, List<PrimePartEntity>>,
        setEntityMap: Map<String, PrimeSetEntity>
    ): PrimeSetDomain? {
        val set = setEntityMap[setId] ?: return null

        fun mapPart(part: PrimePartEntity, multiplier: Int): PrimePartDomain {
            val comps = componentMap[part.id] ?: emptyList()
            val relicRewards = comps.map { c ->
                val relic = relicMap[c.relicId]
                val formattedName = if (relic != null) "${
                    StringFormatter.capitalizeWords(relic.era.name.lowercase())
                } ${relic.name}" else ""
                RelicRewardDomain(formattedName, c.rarity, relic?.source ?: RelicSource.VAULT)
            }
            val sources = comps.mapNotNull { relicMap[it.relicId]?.source }
            val bestSource = sources.minByOrNull { it.ordinal } ?: RelicSource.VAULT

            val nested = if (part.part == PrimePartType.PRIME_SET) {
                resolvePartsInternal(part.id, multiplier * part.quantity, partsBySetMap, componentMap, ::mapPart)
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

        return PrimeSetDomain(
            id = set.id,
            name = set.name,
            type = set.type,
            imageUrl = set.image,
            parts = resolvePartsInternal(set.id, 1, partsBySetMap, componentMap, ::mapPart).sortedBy { it.name }
        )
    }

    private fun resolvePartsInternal(
        id: String,
        multiplier: Int,
        partsBySetMap: Map<String, List<PrimePartEntity>>,
        componentMap: Map<String, List<PrimeComponentEntity>>,
        mapper: (PrimePartEntity, Int) -> PrimePartDomain
    ): List<PrimePartDomain> {
        val setParts = partsBySetMap[id] ?: emptyList()
        val hasBlueprint = setParts.any { it.id == id }

        return if (!hasBlueprint) {
            val comps = componentMap[id] ?: emptyList()
            if (comps.isNotEmpty()) {
                val blueprint = mapper(
                    PrimePartEntity(id, id, PrimePartType.BLUEPRINT, 1),
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
}

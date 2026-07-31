package com.felipimatheuz.primehunt.domain.util

import com.felipimatheuz.primehunt.data.remote.entity.PrimeComponentEntity
import com.felipimatheuz.primehunt.data.remote.entity.PrimePartEntity
import com.felipimatheuz.primehunt.data.remote.enums.PrimePartType

object PrimeSetResolver {

    /**
     * Resolves all required parts for a given set, handling nested sets recursively.
     * Returns a map of partId to the total quantity needed.
     */
    fun resolveRequiredParts(
        setId: String,
        multiplier: Int,
        partsBySetMap: Map<String, List<PrimePartEntity>>,
        componentByPartMap: Map<String, List<PrimeComponentEntity>>,
        result: MutableMap<String, Int> = mutableMapOf()
    ): Map<String, Int> {
        val parts = partsBySetMap[setId] ?: emptyList()
        val hasExplicitBlueprint = parts.any { it.id == setId }

        // If there's no explicit blueprint part, but there are components for this setId,
        // it means the set itself acts as a blueprint (synthesized).
        if (!hasExplicitBlueprint) {
            val comps = componentByPartMap[setId] ?: emptyList()
            if (comps.isNotEmpty()) {
                result[setId] = (result[setId] ?: 0) + multiplier
            }
        }

        parts.forEach { part ->
            if (part.part == PrimePartType.PRIME_SET) {
                // Recursively resolve nested sets
                resolveRequiredParts(
                    part.id,
                    multiplier * part.quantity,
                    partsBySetMap,
                    componentByPartMap,
                    result
                )
            } else {
                result[part.id] = (result[part.id] ?: 0) + multiplier * part.quantity
            }
        }
        
        return result
    }
}

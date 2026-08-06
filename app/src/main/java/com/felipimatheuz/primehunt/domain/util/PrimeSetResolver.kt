package com.felipimatheuz.primehunt.domain.util

import com.felipimatheuz.primehunt.data.remote.enums.PrimePartType

object PrimeSetResolver {

    data class ResolvePart(
        val id: String,
        val part: PrimePartType,
        val quantity: Int
    )

    /**
     * Resolves all required parts for a given set, handling nested sets recursively.
     * Returns a map of partId to the total quantity needed.
     */
    fun resolveRequiredParts(
        setId: String,
        multiplier: Int,
        partsBySetMap: Map<String, List<ResolvePart>>,
        hasComponentsMap: Map<String, Boolean>,
        result: MutableMap<String, Int> = mutableMapOf()
    ): Map<String, Int> {
        val parts = partsBySetMap[setId] ?: emptyList()
        val hasExplicitBlueprint = parts.any { it.id == setId }

        // If there's no explicit blueprint part, but there are components for this setId,
        // it means the set itself acts as a blueprint (synthesized).
        if (!hasExplicitBlueprint) {
            if (hasComponentsMap[setId] == true) {
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
                    hasComponentsMap,
                    result
                )
            } else {
                result[part.id] = (result[part.id] ?: 0) + multiplier * part.quantity
            }
        }
        
        return result
    }
}

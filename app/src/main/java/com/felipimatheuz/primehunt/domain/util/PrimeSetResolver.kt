package com.felipimatheuz.primehunt.domain.util

import com.felipimatheuz.primehunt.domain.model.enums.PrimePartType

object PrimeSetResolver {

    data class ResolvePart(
        val id: String,
        val part: PrimePartType,
        val quantity: Int
    )

    fun resolveRequiredParts(
        setId: String,
        multiplier: Int,
        partsBySetMap: Map<String, List<ResolvePart>>,
        hasComponentsMap: Map<String, Boolean>,
        result: MutableMap<String, Int> = mutableMapOf()
    ): Map<String, Int> {
        val parts = partsBySetMap[setId] ?: emptyList()
        val hasExplicitBlueprint = parts.any { it.id == setId }

        if (!hasExplicitBlueprint) {
            if (hasComponentsMap[setId] == true) {
                result[setId] = (result[setId] ?: 0) + multiplier
            }
        }

        parts.forEach { part ->
            if (part.part == PrimePartType.PRIME_SET) {
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

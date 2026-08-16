package com.felipimatheuz.primehunt.domain.util

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LegacyMigrationParser @Inject constructor() {

    fun parse(data: Map<String, Any>, isSetCategory: Boolean): Map<String, Int> {
        val result = mutableMapOf<String, Int>()

        data.filter { it.value == true }.keys.forEach { rawKey ->
            val segments = rawKey.split("_")
            if (segments.size < 2) return@forEach

            val filteredSegments = if (isSetCategory && segments.size >= 3) {
                segments.drop(1)
            } else {
                segments
            }

            val lastSegment = filteredSegments.last()
            val hasNumericSuffix = lastSegment.toIntOrNull() != null

            val cleanSegments = if (hasNumericSuffix) {
                filteredSegments.dropLast(1)
            } else {
                filteredSegments
            }

            val normalizedPart = when (val partType = cleanSegments.last()) {
                "ULIMB" -> "UPPER_LIMB"
                "LLIMB" -> "LOWER_LIMB"
                "CIRCUIT" -> "SYSTEMS"
                "BLADE" -> if (cleanSegments.first().equals("venka", true)) "BLADES" else partType
                else -> partType
            }

            val finalKey = (cleanSegments.dropLast(1) + normalizedPart).joinToString("_")

            result[finalKey] = (result[finalKey] ?: 0) + 1
        }

        return result
    }

    fun normalizeNewName(name: String): String {
        return name.replace(" Prime", "", ignoreCase = true)
            .replace(" Collar", "", ignoreCase = true)
            .trim()
    }

    fun normalizePrimeSetId(name: String): String {
        return name.replace("_prime", "", ignoreCase = true)
            .uppercase()
            .trim()
    }
}

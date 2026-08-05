package com.felipimatheuz.primehunt.domain.mapper

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LegacyMigrationParser @Inject constructor() {

    /**
     * Transforma o mapa do Firebase (SETS e OTHER) em um mapa consolidado.
     * Chave: NomeItem_Parte (Normalizado)
     * Valor: Quantidade total encontrada
     */
    fun parse(data: Map<String, Any>, isSetCategory: Boolean): Map<String, Int> {
        val result = mutableMapOf<String, Int>()

        data.filter { it.value == true }.keys.forEach { rawKey ->
            val segments = rawKey.split("_")
            if (segments.size < 2) return@forEach

            // 1. Harmonização do Prefixo
            // Se for categoria SET, removemos o prefixo do bundle (ex: Gauss_Acceltra_BP -> Acceltra_BP)
            val filteredSegments = if (isSetCategory && segments.size >= 3) {
                segments.drop(1)
            } else {
                segments
            }

            // 2. Extração de Quantidade e Limpeza do Sufixo Numérico
            // Verifica se o último segmento é um número (ex: _0, _1)
            val lastSegment = filteredSegments.last()
            val hasNumericSuffix = lastSegment.toIntOrNull() != null
            
            val cleanSegments = if (hasNumericSuffix) {
                filteredSegments.dropLast(1)
            } else {
                filteredSegments
            }

            // 3. Normalização de Partes (Nomenclatura antiga -> nova)
            val normalizedPart = when (val partType = cleanSegments.last()) {
                "ULIMB" -> "UPPER_LIMB"
                "LLIMB" -> "LOWER_LIMB"
                "BLADE" -> if (cleanSegments.first().equals("venka", true)) "BLADES"  else partType// Por padrão migramos para o plural, CloudRepository filtrará se necessário
                else -> partType
            }

            val finalKey = (cleanSegments.dropLast(1) + normalizedPart).joinToString("_")
            
            // 4. Acúmulo de Quantidade
            result[finalKey] = (result[finalKey] ?: 0) + 1
        }

        return result
    }

    /**
     * Normaliza nomes do banco novo para comparação com o legado
     * ex: "Ash Prime" -> "Ash"
     */
    fun normalizeNewName(name: String): String {
        return name.replace(" Prime", "", ignoreCase = true).trim()
    }
}

package com.felipimatheuz.primehunt.domain.mapper

import com.felipimatheuz.primehunt.data.remote.enums.PrimePartType

object PrimeMapper {
    
    fun capitalizeWords(text: String): String =
        text.split(" ").joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }

    fun formatPartName(setName: String, partType: PrimePartType): String {
        return "$setName ${capitalizeWords(partType.name.replace("_", " ").lowercase())}"
    }

    fun getBlueprintName(setName: String): String {
        return "$setName Blueprint"
    }
}

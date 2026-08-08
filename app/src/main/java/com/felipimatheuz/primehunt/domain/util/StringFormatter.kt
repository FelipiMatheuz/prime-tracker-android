package com.felipimatheuz.primehunt.domain.util

import com.felipimatheuz.primehunt.domain.model.enums.PrimePartType
import java.util.Locale

object StringFormatter {
    
    fun capitalizeWords(text: String): String =
        text.split(" ").joinToString(" ") { 
            it.replaceFirstChar { char -> 
                if (char.isLowerCase()) char.titlecase(Locale.getDefault()) else char.toString() 
            } 
        }

    fun formatPartName(setName: String, partType: PrimePartType): String {
        return "$setName ${capitalizeWords(partType.name.replace("_", " ").lowercase())}"
    }

    fun getBlueprintName(setName: String): String {
        return "$setName Blueprint"
    }
}

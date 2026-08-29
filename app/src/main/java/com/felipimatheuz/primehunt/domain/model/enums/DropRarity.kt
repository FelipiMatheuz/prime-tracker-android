package com.felipimatheuz.primehunt.domain.model.enums

enum class DropRarity {
    COMMON,
    UNCOMMON,
    RARE;

    companion object {
        fun fromString(value: String): DropRarity {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: COMMON
        }
    }
}

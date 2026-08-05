package com.felipimatheuz.primehunt.data.remote.enums

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

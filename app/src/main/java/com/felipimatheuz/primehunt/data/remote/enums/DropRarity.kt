package com.felipimatheuz.primehunt.data.remote.enums

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.felipimatheuz.primehunt.ui.theme.Common
import com.felipimatheuz.primehunt.ui.theme.CommonDark
import com.felipimatheuz.primehunt.ui.theme.Rare
import com.felipimatheuz.primehunt.ui.theme.RareDark
import com.felipimatheuz.primehunt.ui.theme.Uncommon
import com.felipimatheuz.primehunt.ui.theme.UncommonDark

enum class DropRarity(val lightColor: Color, val darkColor: Color) {
    COMMON(Common, CommonDark),
    UNCOMMON(Uncommon, UncommonDark),
    RARE(Rare, RareDark);

    @Composable
    fun getColor(): Color {
        val isDark = isSystemInDarkTheme()
        return if (isDark) darkColor else lightColor
    }

    companion object {
        fun fromString(value: String): DropRarity {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: COMMON
        }
    }
}
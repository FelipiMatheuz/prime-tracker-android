package com.felipimatheuz.primehunt.domain.model.enums

import androidx.annotation.StringRes
import com.felipimatheuz.primehunt.R

enum class AppTheme(@StringRes val displayNameRes: Int) {
    SYSTEM(R.string.settings_theme_option_system),
    LIGHT(R.string.settings_theme_option_light),
    DARK(R.string.settings_theme_option_dark)
}

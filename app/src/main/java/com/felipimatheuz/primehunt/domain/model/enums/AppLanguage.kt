package com.felipimatheuz.primehunt.domain.model.enums

import androidx.annotation.StringRes
import com.felipimatheuz.primehunt.R

enum class AppLanguage(
    @StringRes val displayNameRes: Int,
    val languageTag: String?
) {
    SYSTEM(R.string.settings_language_option_system, null),
    ENGLISH(R.string.settings_language_option_english, "en"),
    GERMAN(R.string.settings_language_option_german, "de-DE"),
    SPANISH(R.string.settings_language_option_spanish, "es-ES"),
    FRENCH(R.string.settings_language_option_french, "fr-FR"),
    PORTUGUESE_BR(R.string.settings_language_option_portuguese_br, "pt-BR"),
    CHINESE_CN(R.string.settings_language_option_chinese_cn, "zh-CN")
}

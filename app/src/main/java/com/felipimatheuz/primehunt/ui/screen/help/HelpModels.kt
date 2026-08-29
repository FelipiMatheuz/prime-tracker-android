package com.felipimatheuz.primehunt.ui.screen.help

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class HelpSection(
    @StringRes val title: Int,
    @DrawableRes val icon: Int? = null,
    @StringRes val description: Int? = null,
    val subSections: List<HelpSubSection> = emptyList(),
    @StringRes val tip: Int? = null,
    val faqItems: List<FaqItem> = emptyList(),
)

data class HelpSubSection(
    @StringRes val title: Int,
    @StringRes val description: Int,
    @StringRes val tip: Int? = null
)

data class FaqItem(
    @StringRes val question: Int,
    @StringRes val answer: Int
)

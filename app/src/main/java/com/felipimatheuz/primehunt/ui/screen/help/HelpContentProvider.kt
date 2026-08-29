package com.felipimatheuz.primehunt.ui.screen.help

import com.felipimatheuz.primehunt.R

object HelpContentProvider {
    val helpSections = listOf(
        HelpSection(
            title = R.string.help_gs_title,
            description = R.string.help_gs_desc,
            tip = R.string.help_gs_tip
        ),
        HelpSection(
            title = R.string.help_ps_title,
            icon = R.drawable.ic_prime,
            description = R.string.help_ps_desc,
            subSections = listOf(
                HelpSubSection(
                    title = R.string.help_ps_inv_title,
                    description = R.string.help_ps_inv_desc,
                    tip = R.string.help_ps_inv_tip
                )
            )
        ),
        HelpSection(
            title = R.string.help_relics_title,
            icon = R.drawable.ic_relic,
            description = R.string.help_relics_desc,
            subSections = listOf(
                HelpSubSection(
                    title = R.string.help_relics_av_title,
                    description = R.string.help_relics_av_desc,
                    tip = R.string.help_relics_av_tip
                )
            )
        ),
        HelpSection(
            title = R.string.help_goals_title,
            icon = R.drawable.ic_waypoint,
            description = R.string.help_goals_desc,
            subSections = listOf(
                HelpSubSection(
                    title = R.string.help_goals_tags_title,
                    description = R.string.help_goals_tags_desc,
                    tip = R.string.help_goals_tags_tip
                )
            )
        ),
        HelpSection(
            title = R.string.help_cloud_title,
            icon = R.drawable.ic_transference,
            description = R.string.help_cloud_desc,
            subSections = listOf(
                HelpSubSection(
                    title = R.string.help_cloud_legacy_title,
                    description = R.string.help_cloud_legacy_desc,
                    tip = R.string.help_cloud_legacy_tip
                )
            )
        ),
        HelpSection(
            title = R.string.help_faq_title,
            icon = R.drawable.ic_help,
            faqItems = listOf(
                FaqItem(
                    question = R.string.help_faq_q1,
                    answer = R.string.help_faq_a1
                ),
                FaqItem(
                    question = R.string.help_faq_q2,
                    answer = R.string.help_faq_a2
                ),
                FaqItem(
                    question = R.string.help_faq_q3,
                    answer = R.string.help_faq_a3
                ),
                FaqItem(
                    question = R.string.help_faq_q4,
                    answer = R.string.help_faq_a4
                ),
                FaqItem(
                    question = R.string.help_faq_q5,
                    answer = R.string.help_faq_a5
                )
            )
        )
    )
}

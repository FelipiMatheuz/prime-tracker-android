package com.felipimatheuz.primehunt.data.local.enums

import androidx.annotation.DrawableRes
import com.felipimatheuz.primehunt.R

enum class GoalIcons (@param:DrawableRes val icon: Int) {
    SLASH(R.drawable.tag_slash),
    PUNCTURE(R.drawable.tag_puncture),
    IMPACT(R.drawable.tag_impact),
    HEAT(R.drawable.tag_heat),
    COLD(R.drawable.tag_cold),
    ELECTRICITY(R.drawable.tag_electricity),
    TOXIN(R.drawable.tag_toxin),
    CORROSIVE(R.drawable.tag_corrosive),
    VIRAL(R.drawable.tag_viral),
    GAS(R.drawable.tag_gas),
    RADIATION(R.drawable.tag_radiation),
    MAGNETIC(R.drawable.tag_magnetic),
    BLAST(R.drawable.tag_blast),
    VOID(R.drawable.tag_void),
    TAU(R.drawable.tag_tau),
    TRUE(R.drawable.tag_true_dmg)
}
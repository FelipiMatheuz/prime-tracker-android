package com.felipimatheuz.primehunt.domain.model.squad

import androidx.annotation.StringRes
import com.felipimatheuz.primehunt.R

enum class Platform(@StringRes val labelRes: Int) {
    PC(R.string.platform_pc),
    PLAYSTATION(R.string.platform_playstation),
    XBOX(R.string.platform_xbox),
    NINTENDO_SWITCH(R.string.platform_switch),
    CROSSPLAY(R.string.platform_crossplay)
}

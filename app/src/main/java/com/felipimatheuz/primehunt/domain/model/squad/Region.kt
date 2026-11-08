package com.felipimatheuz.primehunt.domain.model.squad

import androidx.annotation.StringRes
import com.felipimatheuz.primehunt.R

enum class Region(@StringRes val labelRes: Int) {
    NORTH_AMERICA(R.string.region_north_america),
    SOUTH_AMERICA(R.string.region_south_america),
    EUROPE(R.string.region_europe),
    ASIA(R.string.region_asia),
    OCEANIA(R.string.region_oceania)
}

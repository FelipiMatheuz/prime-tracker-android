package com.felipimatheuz.primehunt.ui.viewmodel.relic

import androidx.annotation.StringRes
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.data.remote.enums.RelicEra
import com.felipimatheuz.primehunt.data.remote.enums.RelicSource
import com.felipimatheuz.primehunt.domain.model.RelicDomain
import com.felipimatheuz.primehunt.ui.mvi.MviIntent
import com.felipimatheuz.primehunt.ui.mvi.MviState
import com.felipimatheuz.primehunt.ui.viewmodel.primeset.ProgressFilter

enum class RelicsView(val displayNameRes: Int) {
    ERA(R.string.view_relic_era),
    AVAILABILITY(R.string.view_relic_availability),
    PROGRESS(R.string.view_relic_progress)
}

sealed class RelicGroup {
    data object Lith : RelicGroup()
    data object Meso : RelicGroup()
    data object Neo : RelicGroup()
    data object Axi: RelicGroup()
    data object Available : RelicGroup()
    data object Events : RelicGroup()
    data object Vaulted : RelicGroup()
    data object Completed : RelicGroup()
    data object Missing1to3 : RelicGroup()
    data object Missing4Plus : RelicGroup()

    @get:StringRes
    val titleRes: Int
        get() = when (this) {
            Lith -> R.string.relic_era_lith
            Meso -> R.string.relic_era_meso
            Neo -> R.string.relic_era_neo
            Axi -> R.string.relic_era_axi
            Available -> R.string.relic_group_available
            Events -> R.string.relic_group_events
            Vaulted -> R.string.relic_origin_vaulted
            Completed -> R.string.relic_completed
            Missing1to3 -> R.string.relic_group_missing_1_3
            Missing4Plus -> R.string.relic_group_missing_4_plus
        }
}

data class RelicFilters(
    val eras: Set<RelicEra> = emptySet(),
    val availabilities: Set<RelicSource> = emptySet(),
    val progress: ProgressFilter = ProgressFilter.ALL
) {
    val activeCount: Int
        get() {
            var count = 0
            count += eras.size
            count += availabilities.size
            if (progress != ProgressFilter.ALL) count++
            return count
        }
}

data class RelicState(
    val relics: List<RelicDomain> = emptyList(),
    val groupedRelics: Map<RelicGroup, List<RelicDomain>> = emptyMap(),
    val queryFilter: String = "",
    val activeFilters: RelicFilters = RelicFilters(),
    val selectedView: RelicsView = RelicsView.ERA,
    val isLoading: Boolean = true
) : MviState

sealed class RelicIntent : MviIntent {
    data class Search(val query: String) : RelicIntent()
    object ClearSearch : RelicIntent()
    data class UpdateFilters(val filters: RelicFilters) : RelicIntent()
    data class ChangeView(val view: RelicsView) : RelicIntent()
}

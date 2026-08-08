package com.felipimatheuz.primehunt.domain.model.prefs

import com.felipimatheuz.primehunt.domain.model.enums.GoalStatus
import com.felipimatheuz.primehunt.domain.model.enums.GoalTargetType
import com.felipimatheuz.primehunt.domain.model.enums.ProgressFilter
import com.felipimatheuz.primehunt.domain.model.enums.RelicsView
import com.felipimatheuz.primehunt.domain.model.enums.PrimeType
import com.felipimatheuz.primehunt.domain.model.enums.RelicEra
import com.felipimatheuz.primehunt.domain.model.enums.RelicSource

data class PrimeSetUiPrefs(
    val selectedView: Int = 0,
    val progress: ProgressFilter = ProgressFilter.ALL,
    val categories: Set<PrimeType> = emptySet(),
    val availabilities: Set<RelicSource> = emptySet()
)

data class RelicUiPrefs(
    val selectedView: RelicsView = RelicsView.ERA,
    val eras: Set<RelicEra> = emptySet(),
    val availabilities: Set<RelicSource> = emptySet(),
    val progress: ProgressFilter = ProgressFilter.ALL
)

data class GoalUiPrefs(
    val status: GoalStatus = GoalStatus.ACTIVE,
    val targetTypes: Set<GoalTargetType> = GoalTargetType.entries.toSet(),
    val categoryIds: Set<Long> = emptySet()
)

data class CloudUiPrefs(
    val isMigrationSuccess: Boolean = false
)

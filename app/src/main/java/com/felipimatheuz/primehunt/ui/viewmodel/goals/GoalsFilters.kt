package com.felipimatheuz.primehunt.ui.viewmodel.goals

import com.felipimatheuz.primehunt.data.local.enums.GoalStatus
import com.felipimatheuz.primehunt.data.local.enums.GoalTargetType

data class GoalsFilters(
    val status: GoalStatus = GoalStatus.ACTIVE,
    val targetTypes: Set<GoalTargetType> = GoalTargetType.entries.toSet(),
    val categoryIds: Set<Long> = emptySet(),
    val allCategoryIds: Set<Long> = emptySet()
) {
    val activeCount: Int get() {
        var count = 0
        if (status != GoalStatus.ACTIVE) count++
        if (targetTypes != GoalTargetType.entries.toSet()) count++
        if (categoryIds.isNotEmpty() && categoryIds != allCategoryIds) count++
        return count
    }
}

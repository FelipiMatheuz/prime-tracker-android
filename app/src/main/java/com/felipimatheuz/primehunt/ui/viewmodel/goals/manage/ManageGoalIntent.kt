package com.felipimatheuz.primehunt.ui.viewmodel.goals.manage

import androidx.compose.ui.graphics.Color
import com.felipimatheuz.primehunt.data.local.entity.GoalTagEntity
import com.felipimatheuz.primehunt.data.local.enums.GoalIcons
import com.felipimatheuz.primehunt.data.local.enums.GoalTargetType
import com.felipimatheuz.primehunt.domain.model.TargetDomain
import com.felipimatheuz.primehunt.ui.mvi.MviIntent

sealed interface ManageGoalIntent : MviIntent {
    data class Initialize(val goalId: Long?) : ManageGoalIntent
    
    // Form Updates
    data class UpdateTargetType(val type: GoalTargetType) : ManageGoalIntent
    data class UpdateQuantity(val quantity: Int) : ManageGoalIntent
    data class UpdateManualQuantity(val quantity: Int) : ManageGoalIntent
    data class SearchTarget(val query: String) : ManageGoalIntent
    data class SelectTarget(val target: TargetDomain) : ManageGoalIntent
    data class SelectTag(val tag: GoalTagEntity) : ManageGoalIntent
    data class UpdateNotes(val notes: String) : ManageGoalIntent
    
    // Actions
    data object SaveGoal : ManageGoalIntent
    data object DeleteGoal : ManageGoalIntent
    data object CompleteGoal : ManageGoalIntent
    data object ResetOperation : ManageGoalIntent
    
    // Tag Creation
    data object ShowTagSheet : ManageGoalIntent
    data object HideTagSheet : ManageGoalIntent
    data class UpdateNewTagName(val name: String) : ManageGoalIntent
    data class UpdateNewTagIcon(val icon: GoalIcons) : ManageGoalIntent
    data class UpdateNewTagColor(val color: Color) : ManageGoalIntent
    data object CreateTag : ManageGoalIntent
}

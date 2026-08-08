package com.felipimatheuz.primehunt.ui.viewmodel.goals.manage

import androidx.compose.ui.graphics.Color
import com.felipimatheuz.primehunt.domain.model.enums.GoalIcons
import com.felipimatheuz.primehunt.domain.model.enums.GoalStatus
import com.felipimatheuz.primehunt.domain.model.enums.GoalTargetType
import com.felipimatheuz.primehunt.domain.model.GoalTagDomain
import com.felipimatheuz.primehunt.domain.model.TargetDomain
import com.felipimatheuz.primehunt.ui.mvi.MviState

data class ManageGoalState(
    val goalId: Long? = null,
    val isEditMode: Boolean = false,
    val status: GoalStatus = GoalStatus.ACTIVE,

    val availableTags: List<GoalTagDomain> = emptyList(),
    val suggestions: List<TargetDomain> = emptyList(),

    val targetType: GoalTargetType = GoalTargetType.PRIME_SET,
    val targetQuery: String = "",
    val selectedTarget: TargetDomain? = null,
    val quantity: Int = 1,
    val manualCurrentQuantity: Int = 0,
    val selectedTag: GoalTagDomain? = null,
    val notes: String = "",

    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val hasChanges: Boolean = false,
    val isFormValid: Boolean = false,
    val operationComplete: Boolean = false,

    val isTagSheetVisible: Boolean = false,
    val newTagName: String = "",
    val newTagIcon: GoalIcons = GoalIcons.SLASH,
    val newTagColor: Color = Color(0xFF673AB7),
    val isTagCreationValid: Boolean = false
) : MviState

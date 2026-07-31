package com.felipimatheuz.primehunt.ui.viewmodel.goals.manage

import androidx.compose.ui.graphics.Color
import com.felipimatheuz.primehunt.data.local.entity.GoalTagEntity
import com.felipimatheuz.primehunt.data.local.enums.GoalIcons
import com.felipimatheuz.primehunt.data.local.enums.GoalStatus
import com.felipimatheuz.primehunt.data.local.enums.GoalTargetType
import com.felipimatheuz.primehunt.domain.model.TargetDomain
import com.felipimatheuz.primehunt.ui.mvi.MviState

data class ManageGoalState(
    val goalId: Long? = null,
    val isEditMode: Boolean = false,
    val status: GoalStatus = GoalStatus.ACTIVE,
    
    // Loaded Data
    val availableTags: List<GoalTagEntity> = emptyList(),
    val suggestions: List<TargetDomain> = emptyList(),
    
    // Form Fields
    val targetType: GoalTargetType = GoalTargetType.PRIME_SET,
    val targetQuery: String = "",
    val selectedTarget: TargetDomain? = null,
    val quantity: Int = 1,
    val manualCurrentQuantity: Int = 0,
    val selectedTag: GoalTagEntity? = null,
    val notes: String = "",
    
    // UI State
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val hasChanges: Boolean = false,
    val isFormValid: Boolean = false,
    val operationComplete: Boolean = false,
    
    // Tag Creation Modal
    val isTagSheetVisible: Boolean = false,
    val newTagName: String = "",
    val newTagIcon: GoalIcons = GoalIcons.WARFRAME,
    val newTagColor: Color = Color(0xFF673AB7),
    val isTagCreationValid: Boolean = false
) : MviState

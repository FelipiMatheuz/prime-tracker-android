package com.felipimatheuz.primehunt.ui.viewmodel.goals.manage

import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felipimatheuz.primehunt.data.local.dao.GoalDao
import com.felipimatheuz.primehunt.data.local.dao.GoalTagDao
import com.felipimatheuz.primehunt.data.local.entity.GoalEntity
import com.felipimatheuz.primehunt.data.local.entity.GoalTagEntity
import com.felipimatheuz.primehunt.data.local.enums.GoalStatus
import com.felipimatheuz.primehunt.data.local.enums.GoalTargetType
import com.felipimatheuz.primehunt.domain.model.GoalDomain
import com.felipimatheuz.primehunt.domain.model.TargetDomain
import com.felipimatheuz.primehunt.domain.repository.GoalRepository
import com.felipimatheuz.primehunt.domain.usecase.goal.GetGoalsUseCase
import com.felipimatheuz.primehunt.ui.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class)
@HiltViewModel
class ManageGoalViewModel @Inject constructor(
    private val repository: GoalRepository,
    private val getGoalsUseCase: GetGoalsUseCase,
    private val goalDao: GoalDao,
    private val tagDao: GoalTagDao
) : ViewModel(), MviViewModel<ManageGoalState, ManageGoalIntent> {

    private val _state = MutableStateFlow(ManageGoalState())
    override val state: StateFlow<ManageGoalState> = _state.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    private var allTargets: List<TargetDomain> = emptyList()
    private var originalGoal: GoalDomain? = null

    init {
        tagDao.observeAll()
            .onEach { tags ->
                _state.update { it.copy(availableTags = tags) }
            }
            .launchIn(viewModelScope)

        getGoalsUseCase.observeAllTargets()
            .onEach { targets ->
                allTargets = targets
                if (!_state.value.isEditMode && _state.value.selectedTarget == null && _state.value.targetQuery.isEmpty()) {
                    autoSelectFirstTarget()
                }
            }
            .launchIn(viewModelScope)

        _searchQuery
            .debounce(200.milliseconds)
            .distinctUntilChanged()
            .onEach { query ->
                if (query.length >= 2) {
                    val filtered = allTargets.filter {
                        it.type == _state.value.targetType && it.name.contains(query, ignoreCase = true)
                    }.take(5)
                    _state.update { it.copy(suggestions = filtered) }
                } else {
                    _state.update { it.copy(suggestions = emptyList()) }
                }
            }
            .launchIn(viewModelScope)
    }

    override fun onIntent(intent: ManageGoalIntent) {
        when (intent) {
            is ManageGoalIntent.Initialize -> initialize(intent.goalId)
            is ManageGoalIntent.UpdateTargetType -> updateTargetType(intent.type)
            is ManageGoalIntent.UpdateQuantity -> {
                _state.update { it.copy(quantity = maxOf(1, intent.quantity)) }
                validateAndCheckChanges()
            }
            is ManageGoalIntent.UpdateManualQuantity -> {
                _state.update { it.copy(manualCurrentQuantity = intent.quantity.coerceIn(0, it.quantity)) }
                validateAndCheckChanges()
            }
            is ManageGoalIntent.SearchTarget -> {
                _state.update { it.copy(targetQuery = intent.query, selectedTarget = null) }
                _searchQuery.value = intent.query
                validateAndCheckChanges()
            }
            is ManageGoalIntent.SelectTarget -> selectTarget(intent.target)
            is ManageGoalIntent.SelectTag -> {
                _state.update { it.copy(selectedTag = intent.tag) }
                validateAndCheckChanges()
            }
            is ManageGoalIntent.UpdateNotes -> {
                _state.update { it.copy(notes = intent.notes) }
                validateAndCheckChanges()
            }
            ManageGoalIntent.SaveGoal -> saveGoal()
            ManageGoalIntent.DeleteGoal -> deleteGoal()
            ManageGoalIntent.CompleteGoal -> completeGoal()
            ManageGoalIntent.ResetOperation -> _state.update { it.copy(operationComplete = false) }
            ManageGoalIntent.ShowTagSheet -> _state.update { it.copy(isTagSheetVisible = true) }
            ManageGoalIntent.HideTagSheet -> _state.update { it.copy(isTagSheetVisible = false) }
            is ManageGoalIntent.UpdateNewTagName -> _state.update { it.copy(newTagName = intent.name, isTagCreationValid = intent.name.isNotBlank()) }
            is ManageGoalIntent.UpdateNewTagIcon -> _state.update { it.copy(newTagIcon = intent.icon) }
            is ManageGoalIntent.UpdateNewTagColor -> _state.update { it.copy(newTagColor = intent.color) }
            ManageGoalIntent.CreateTag -> createTag()
        }
    }

    private fun initialize(goalId: Long?) {
        // Reset state for new initialization
        originalGoal = null
        _state.update { 
            ManageGoalState(
                availableTags = it.availableTags, // Keep available tags to avoid flickering
                isEditMode = goalId != null && goalId != -1L,
                goalId = goalId,
                isLoading = goalId != null && goalId != -1L
            )
        }

        if (goalId == null || goalId == -1L) {
            autoSelectFirstTarget()
            return
        }

        viewModelScope.launch {
            getGoalsUseCase.observeGoal(goalId).collect { goal ->
                if (goal != null && originalGoal == null) {
                    originalGoal = goal
                    _state.update {
                        it.copy(
                            targetType = goal.targetType,
                            targetQuery = goal.targetName,
                            quantity = goal.desiredQuantity,
                            manualCurrentQuantity = goal.currentQuantity,
                            selectedTag = it.availableTags.find { t -> t.id == goal.tag.id } ?: GoalTagEntity(goal.tag.id, goal.tag.name, goal.tag.icon, goal.tag.color),
                            notes = goal.note ?: "",
                            status = goal.status,
                            isLoading = false
                        )
                    }
                    validateAndCheckChanges()
                } else if (goal == null) {
                    _state.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    private fun autoSelectFirstTarget() {
        val first = allTargets.firstOrNull { it.type == _state.value.targetType }
        _state.update {
            it.copy(
                selectedTarget = first,
                targetQuery = first?.name ?: "",
                suggestions = emptyList()
            )
        }
        validateAndCheckChanges()
    }

    private fun updateTargetType(type: GoalTargetType) {
        val firstTarget = allTargets.firstOrNull { it.type == type }
        _state.update {
            it.copy(
                targetType = type,
                selectedTarget = firstTarget,
                targetQuery = firstTarget?.name ?: "",
                suggestions = emptyList()
            )
        }
        validateAndCheckChanges()
    }

    private fun selectTarget(target: TargetDomain) {
        _state.update { it.copy(selectedTarget = target, targetQuery = target.name, suggestions = emptyList()) }
        validateAndCheckChanges()
    }

    private fun validateAndCheckChanges() {
        val s = _state.value
        val isValid = (s.isEditMode || s.selectedTarget != null) && s.quantity > 0 && s.selectedTag != null
        
        var hasChanges = false
        originalGoal?.let { goal ->
            hasChanges = s.quantity != goal.desiredQuantity ||
                    s.selectedTag?.id != goal.tag.id ||
                    s.notes != (goal.note ?: "") ||
                    s.manualCurrentQuantity != goal.currentQuantity
        }

        _state.update { it.copy(isFormValid = isValid, hasChanges = hasChanges) }
    }

    private fun saveGoal() {
        val s = _state.value
        if (!s.isFormValid) return

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            val entity = if (s.isEditMode) {
                goalDao.getById(s.goalId!!)?.copy(
                    desiredQuantity = s.quantity,
                    currentQuantity = if (s.targetType == GoalTargetType.RELIC || s.targetType == GoalTargetType.FORMA) s.manualCurrentQuantity else 0,
                    tagId = s.selectedTag!!.id,
                    note = s.notes.ifBlank { null }
                )
            } else {
                GoalEntity(
                    targetType = s.targetType,
                    targetId = s.selectedTarget!!.id,
                    desiredQuantity = s.quantity,
                    currentQuantity = 0, // Defaults to 0 for new goals
                    tagId = s.selectedTag!!.id,
                    status = GoalStatus.ACTIVE,
                    note = s.notes.ifBlank { null },
                    createdAt = System.currentTimeMillis()
                )
            }

            entity?.let { repository.saveGoal(it) }
            _state.update { it.copy(isSaving = false, operationComplete = true) }
        }
    }

    private fun completeGoal() {
        val goalId = _state.value.goalId ?: return
        viewModelScope.launch {
            repository.completeGoal(goalId)
            _state.update { it.copy(operationComplete = true) }
        }
    }

    private fun deleteGoal() {
        val goalId = _state.value.goalId ?: return
        viewModelScope.launch {
            repository.deleteGoal(goalId)
            _state.update { it.copy(operationComplete = true) }
        }
    }

    private fun createTag() {
        val s = _state.value
        if (!s.isTagCreationValid) return

        viewModelScope.launch {
            val tag = GoalTagEntity(
                name = s.newTagName,
                icon = s.newTagIcon,
                color = s.newTagColor.toArgb()
            )
            repository.saveTag(tag)
            tagDao.observeAll().firstOrNull()?.find { it.name == s.newTagName }?.let { newTag ->
                _state.update { 
                    it.copy(
                        selectedTag = newTag,
                        isTagSheetVisible = false,
                        newTagName = "",
                        isTagCreationValid = false
                    ) 
                }
                validateAndCheckChanges()
            }
        }
    }
}

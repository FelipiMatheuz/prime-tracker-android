package com.felipimatheuz.primehunt.ui.screen.goals.manage

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.data.local.enums.GoalStatus
import com.felipimatheuz.primehunt.data.local.enums.GoalTargetType
import com.felipimatheuz.primehunt.ui.screen.goals.components.GoalForm
import com.felipimatheuz.primehunt.ui.screen.goals.components.GoalFormActions
import com.felipimatheuz.primehunt.ui.screen.goals.components.GoalFormState
import com.felipimatheuz.primehunt.ui.screen.goals.components.ManageGoalSkeleton
import com.felipimatheuz.primehunt.ui.screen.goals.components.TagCreationBottomSheet
import com.felipimatheuz.primehunt.ui.theme.PrimeTrackerTheme
import com.felipimatheuz.primehunt.ui.viewmodel.goals.manage.ManageGoalIntent
import com.felipimatheuz.primehunt.ui.viewmodel.goals.manage.ManageGoalState
import com.felipimatheuz.primehunt.ui.viewmodel.goals.manage.ManageGoalViewModel

@Composable
fun ManageGoalScreen(
    goalId: Long?,
    navigationId: String,
    paddingValues: PaddingValues,
    onBack: () -> Unit,
    viewModel: ManageGoalViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(navigationId) {
        viewModel.onIntent(ManageGoalIntent.Initialize(goalId))
    }

    LaunchedEffect(state.operationComplete) {
        if (state.operationComplete) {
            onBack()
            viewModel.onIntent(ManageGoalIntent.ResetOperation)
        }
    }

    ManageGoalContent(
        paddingValues = paddingValues,
        state = state,
        onIntent = viewModel::onIntent,
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageGoalContent(
    paddingValues: PaddingValues,
    state: ManageGoalState,
    onIntent: (ManageGoalIntent) -> Unit,
    onBack: () -> Unit
) {
    val tagSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    val isCompleted = state.status == GoalStatus.COMPLETED
    val isFormEnabled = !isCompleted

    LaunchedEffect(state.isEditMode) {
        if (!state.isEditMode) {
            focusRequester.requestFocus()
        }
    }

    LaunchedEffect(state.targetType) {
        if (!state.isEditMode) {
            focusRequester.requestFocus()
        }
    }

    if (state.isLoading) {
        ManageGoalSkeleton(paddingValues = paddingValues)
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if (state.isEditMode) stringResource(R.string.manage_goal_details) else stringResource(R.string.manage_goal_new),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = onBack) {
                Icon(
                    painter = painterResource(R.drawable.ic_close),
                    contentDescription = stringResource(R.string.close)
                )
            }
        }

        GoalForm(
            state = GoalFormState(
                targetType = state.targetType,
                targetQuery = state.targetQuery,
                suggestions = state.suggestions,
                quantity = state.quantity,
                selectedTag = state.selectedTag,
                availableTags = state.availableTags,
                notes = state.notes,
                manualCurrentQuantity = state.manualCurrentQuantity,
                readOnlyTarget = state.isEditMode,
                showManualQuantity = state.isEditMode && (state.targetType == GoalTargetType.RELIC || state.targetType == GoalTargetType.FORMA),
                enabled = isFormEnabled,
                focusRequester = if (!state.isEditMode) focusRequester else null
            ),
            actions = GoalFormActions(
                onTargetTypeChange = { onIntent(ManageGoalIntent.UpdateTargetType(it)) },
                onTargetQueryChange = { onIntent(ManageGoalIntent.SearchTarget(it)) },
                onTargetSelected = { onIntent(ManageGoalIntent.SelectTarget(it)) },
                onQuantityChange = { onIntent(ManageGoalIntent.UpdateQuantity(it)) },
                onTagSelected = { onIntent(ManageGoalIntent.SelectTag(it)) },
                onCreateTagClick = { onIntent(ManageGoalIntent.ShowTagSheet) },
                onNotesChange = { onIntent(ManageGoalIntent.UpdateNotes(it)) },
                onManualQuantityChange = { onIntent(ManageGoalIntent.UpdateManualQuantity(it)) }
            )
        )

        Spacer(modifier = Modifier.weight(1f))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (state.isEditMode) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onIntent(ManageGoalIntent.CompleteGoal) },
                        modifier = Modifier.weight(1f),
                        enabled = !isCompleted && !state.isSaving,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = com.felipimatheuz.primehunt.ui.theme.Completed
                        ),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Icon(painterResource(R.drawable.ic_check), contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.manage_goal_action_complete), fontWeight = FontWeight.Bold, maxLines = 1)
                    }

                    OutlinedButton(
                        onClick = { showDeleteConfirmation = true },
                        modifier = Modifier.weight(1f),
                        enabled = !isCompleted && !state.isSaving,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        border = BorderStroke(1.dp, if (isCompleted) MaterialTheme.colorScheme.outline.copy(alpha = 0.12f) else MaterialTheme.colorScheme.error.copy(alpha = 0.5f)),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Icon(painterResource(R.drawable.ic_delete), contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.manage_goal_action_delete), fontWeight = FontWeight.Bold)
                    }
                }
            }

            Button(
                onClick = { onIntent(ManageGoalIntent.SaveGoal) },
                modifier = Modifier.fillMaxWidth(),
                enabled = isFormEnabled && state.isFormValid && (!state.isEditMode || state.hasChanges) && !state.isSaving,
                shape = MaterialTheme.shapes.medium
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(
                        text = if (state.isEditMode) stringResource(R.string.manage_goal_action_save) else stringResource(R.string.manage_goal_action_create),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    if (state.isTagSheetVisible) {
        TagCreationBottomSheet(
            name = state.newTagName,
            onNameChange = { onIntent(ManageGoalIntent.UpdateNewTagName(it)) },
            selectedIcon = state.newTagIcon,
            onIconChange = { onIntent(ManageGoalIntent.UpdateNewTagIcon(it)) },
            selectedColor = state.newTagColor,
            onColorChange = { onIntent(ManageGoalIntent.UpdateNewTagColor(it)) },
            onCreateClick = { onIntent(ManageGoalIntent.CreateTag) },
            isValid = state.isTagCreationValid,
            onDismiss = { onIntent(ManageGoalIntent.HideTagSheet) },
            sheetState = tagSheetState
        )
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text(stringResource(R.string.manage_goal_delete_title)) },
            text = { Text(stringResource(R.string.manage_goal_delete_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onIntent(ManageGoalIntent.DeleteGoal)
                        showDeleteConfirmation = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(stringResource(R.string.manage_goal_action_delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text(stringResource(R.string.manage_goal_cancel))
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ManageGoalScreenPreview() {
    PrimeTrackerTheme {
        ManageGoalContent(
            paddingValues = PaddingValues(),
            state = ManageGoalState(
                isLoading = false,
                isEditMode = true,
                quantity = 1,
                notes = "Sample note"
            ),
            onIntent = {},
            onBack = {}
        )
    }
}

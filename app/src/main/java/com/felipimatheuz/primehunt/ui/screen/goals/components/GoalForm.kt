package com.felipimatheuz.primehunt.ui.screen.goals.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.data.local.entity.GoalTagEntity
import com.felipimatheuz.primehunt.data.local.enums.GoalTargetType
import com.felipimatheuz.primehunt.domain.model.GoalTagDomain
import com.felipimatheuz.primehunt.domain.model.TargetDomain

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalForm(
    targetType: GoalTargetType,
    onTargetTypeChange: (GoalTargetType) -> Unit,
    quantity: Int,
    onQuantityChange: (Int) -> Unit,
    targetQuery: String,
    onTargetQueryChange: (String) -> Unit,
    suggestions: List<TargetDomain>,
    onTargetSelected: (TargetDomain) -> Unit,
    selectedTag: GoalTagEntity?,
    onTagSelected: (GoalTagEntity) -> Unit,
    availableTags: List<GoalTagEntity>,
    onCreateTagClick: () -> Unit,
    notes: String,
    onNotesChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    readOnlyTarget: Boolean = false,
    focusRequester: FocusRequester? = null,
    showManualQuantity: Boolean = false,
    manualCurrentQuantity: Int = 0,
    onManualQuantityChange: (Int) -> Unit = {}
) {
    val focusManager = LocalFocusManager.current
    var typeDropdownExpanded by remember { mutableStateOf(false) }
    var tagDropdownExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Target Type
            ExposedDropdownMenuBox(
                expanded = typeDropdownExpanded,
                onExpandedChange = { if (!readOnlyTarget) typeDropdownExpanded = it },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = stringResource(targetType.label),
                    onValueChange = {},
                    readOnly = true,
                    enabled = !readOnlyTarget,
                    label = { Text("Target Type") },
                    trailingIcon = { if (!readOnlyTarget) ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeDropdownExpanded) },
                    modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )
                if (!readOnlyTarget) {
                    ExposedDropdownMenu(
                        expanded = typeDropdownExpanded,
                        onDismissRequest = { typeDropdownExpanded = false }
                    ) {
                        GoalTargetType.entries.forEach { type ->
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            painter = painterResource(type.icon),
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(stringResource(type.label))
                                    }
                                },
                                onClick = {
                                    onTargetTypeChange(type)
                                    typeDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Quantity
            OutlinedTextField(
                value = quantity.toString(),
                onValueChange = {
                    it.toIntOrNull()?.let { qty -> onQuantityChange(qty) }
                },
                label = { Text("Quantity") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                modifier = Modifier
                    .width(90.dp)
                    .then(if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier)
            )
        }

        // Target
        if (readOnlyTarget) {
            OutlinedTextField(
                value = targetQuery,
                onValueChange = {},
                readOnly = true,
                enabled = false,
                label = { Text("Target") },
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            AutocompleteTextField(
                value = targetQuery,
                onValueChange = onTargetQueryChange,
                suggestions = suggestions,
                onSuggestionSelected = onTargetSelected,
                label = "Target",
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )
        }

        // Tag Selector
        ExposedDropdownMenuBox(
            expanded = tagDropdownExpanded,
            onExpandedChange = { tagDropdownExpanded = it },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = selectedTag?.name ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Tag") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = tagDropdownExpanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
            )

            ExposedDropdownMenu(
                expanded = tagDropdownExpanded,
                onDismissRequest = { tagDropdownExpanded = false }
            ) {
                availableTags.forEach { tag ->
                    DropdownMenuItem(
                        text = {
                            TagChip(GoalTagDomain(tag.id, tag.name, tag.icon, tag.color))
                        },
                        onClick = {
                            onTagSelected(tag)
                            tagDropdownExpanded = false
                        }
                    )
                }
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(R.drawable.ic_plus),
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Create Tag...", color = MaterialTheme.colorScheme.primary)
                        }
                    },
                    onClick = {
                        onCreateTagClick()
                        tagDropdownExpanded = false
                    }
                )
            }
        }

        // Current Quantity (Manual Progress for Relic/Forma in Edit Mode)
        if (showManualQuantity) {
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Current Progress:",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.surfaceContainerHighest,
                            RoundedCornerShape(20.dp)
                        )
                        .padding(4.dp)
                ) {
                    IconButton(
                        onClick = { onManualQuantityChange(manualCurrentQuantity - 1) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_down_arrow),
                            contentDescription = "Minus",
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Text(
                        text = manualCurrentQuantity.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    IconButton(
                        onClick = { onManualQuantityChange(manualCurrentQuantity + 1) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_up_arrow),
                            contentDescription = "Add",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // Notes
        OutlinedTextField(
            value = notes,
            onValueChange = onNotesChange,
            label = { Text("Notes (Optional)") },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 120.dp),
            singleLine = false,
            minLines = 4,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            )
        )
    }
}

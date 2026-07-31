package com.felipimatheuz.primehunt.ui.screen.goals.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.felipimatheuz.primehunt.data.local.enums.GoalIcons
import com.felipimatheuz.primehunt.domain.model.GoalTagDomain

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagCreationBottomSheet(
    name: String,
    onNameChange: (String) -> Unit,
    selectedIcon: GoalIcons,
    onIconChange: (GoalIcons) -> Unit,
    selectedColor: Color,
    onColorChange: (Color) -> Unit,
    onCreateClick: () -> Unit,
    isValid: Boolean,
    onDismiss: () -> Unit,
    sheetState: SheetState
) {
    var showIconPicker by remember { mutableStateOf(false) }
    var showColorPicker by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Create New Tag",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                label = { Text("Tag Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SelectionCard(
                    label = "Icon",
                    modifier = Modifier.weight(1f),
                    onClick = { showIconPicker = true }
                ) {
                    Icon(
                        painter = painterResource(selectedIcon.icon),
                        contentDescription = null,
                        modifier = Modifier.size(32.dp)
                    )
                }

                SelectionCard(
                    label = "Color",
                    modifier = Modifier.weight(1f),
                    onClick = { showColorPicker = true }
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(MaterialTheme.shapes.small)
                            .background(selectedColor)
                    )
                }
            }

            Text(
                text = "Preview",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                TagChip(
                    tag = GoalTagDomain(
                        id = 0,
                        name = name.ifBlank { "Tag Name" },
                        icon = selectedIcon,
                        color = selectedColor
                    )
                )
            }

            Button(
                onClick = onCreateClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = isValid
            ) {
                Text("Create Tag")
            }
        }
    }

    if (showIconPicker) {
        IconPickerDialog(
            onIconSelected = {
                onIconChange(it)
                showIconPicker = false
            },
            onDismissRequest = { showIconPicker = false }
        )
    }

    if (showColorPicker) {
        ColorPickerDialog(
            onColorSelected = {
                onColorChange(it)
                showColorPicker = false
            },
            onDismissRequest = { showColorPicker = false }
        )
    }
}

@Composable
private fun SelectionCard(
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .clip(MaterialTheme.shapes.medium)
                .clickable { onClick() }
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = MaterialTheme.shapes.medium
                ),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(8.dp)
            ) {
                content()
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Tap to change",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
    }
}

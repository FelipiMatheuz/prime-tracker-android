package com.felipimatheuz.primehunt.ui.screen.goals.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.data.local.entity.GoalTagEntity
import com.felipimatheuz.primehunt.data.local.enums.GoalStatus
import com.felipimatheuz.primehunt.data.local.enums.GoalTargetType
import com.felipimatheuz.primehunt.ui.viewmodel.goals.GoalsFilters

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun GoalsFilterBottomSheet(
    filters: GoalsFilters,
    availableTags: List<GoalTagEntity>,
    onFiltersChanged: (GoalsFilters) -> Unit,
    onDismiss: () -> Unit,
    sheetState: SheetState
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                stringResource(R.string.filter_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Status Filter
            FilterSectionTitle(stringResource(R.string.filter_section_status))
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                GoalStatus.entries.forEachIndexed { index, status ->
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = GoalStatus.entries.size
                        ),
                        onClick = { onFiltersChanged(filters.copy(status = status)) },
                        selected = filters.status == status,
                        label = {
                            Text(
                                stringResource(
                                    when (status) {
                                        GoalStatus.ACTIVE -> R.string.filter_status_active
                                        GoalStatus.COMPLETED -> R.string.filter_status_completed
                                    }
                                )
                            )
                        }
                    )
                }
            }

            // Target Type Filter
            FilterSectionTitle(stringResource(R.string.filter_section_target_type))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                GoalTargetType.entries.forEach { type ->
                    FilterChip(
                        selected = filters.targetTypes.contains(type),
                        onClick = {
                            val newTypes = if (filters.targetTypes.contains(type)) {
                                if (filters.targetTypes.size > 1) filters.targetTypes - type else filters.targetTypes
                            } else {
                                filters.targetTypes + type
                            }
                            onFiltersChanged(filters.copy(targetTypes = newTypes))
                        },
                        label = {
                            Text(
                                stringResource(
                                    when (type) {
                                        GoalTargetType.PRIME_SET -> R.string.goal_target_prime_set
                                        GoalTargetType.PRIME_PART -> R.string.goal_target_prime_part
                                        GoalTargetType.RELIC -> R.string.goal_target_relic
                                        GoalTargetType.FORMA -> R.string.goal_target_forma
                                    }
                                )
                            )
                        }
                    )
                }
            }

            // Category Filter
            if (availableTags.isNotEmpty()) {
                FilterSectionTitle(stringResource(R.string.filter_section_category))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    availableTags.forEach { tag ->
                        FilterChip(
                            selected = filters.categoryIds.contains(tag.id),
                            onClick = {
                                val newCategories = if (filters.categoryIds.contains(tag.id)) {
                                    filters.categoryIds - tag.id
                                } else {
                                    filters.categoryIds + tag.id
                                }
                                onFiltersChanged(filters.copy(categoryIds = newCategories))
                            },
                            label = { Text(tag.name) },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(tag.icon.icon),
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = if (filters.categoryIds.contains(tag.id)) LocalContentColor.current else Color(tag.color)
                                )
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = {
                    onFiltersChanged(
                        GoalsFilters(
                            status = GoalStatus.ACTIVE,
                            targetTypes = GoalTargetType.entries.toSet(),
                            categoryIds = filters.allCategoryIds,
                            allCategoryIds = filters.allCategoryIds
                        )
                    )
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) {
                Text(stringResource(R.string.filter_clear_all))
            }
        }
    }
}

@Composable
private fun FilterSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
    )
}

package com.felipimatheuz.primehunt.ui.screen.goals.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.domain.model.enums.GoalStatus
import com.felipimatheuz.primehunt.domain.model.enums.GoalTargetType
import com.felipimatheuz.primehunt.domain.model.GoalTagDomain
import com.felipimatheuz.primehunt.ui.modifier.PressIntensity
import com.felipimatheuz.primehunt.ui.modifier.pressScale
import com.felipimatheuz.primehunt.ui.screen.components.PrimeSegmentedSelector
import com.felipimatheuz.primehunt.ui.viewmodel.goals.GoalsFilters

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun GoalsFilterBottomSheet(
    filters: GoalsFilters,
    availableTags: List<GoalTagDomain>,
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

            FilterSectionTitle(stringResource(R.string.filter_section_status))
            PrimeSegmentedSelector(
                options = GoalStatus.entries,
                selectedOption = filters.status,
                onOptionClick = { status -> onFiltersChanged(filters.copy(status = status)) },
                labelProvider = { status -> status.displayNameRes
                },
                modifier = Modifier.padding(horizontal = 0.dp)
            )

            FilterSectionTitle(stringResource(R.string.filter_section_target_type))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                GoalTargetType.entries.forEach { type ->
                    val filterTypeInteraction = remember(type) { MutableInteractionSource() }
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
                        interactionSource = filterTypeInteraction,
                        modifier = Modifier.pressScale(
                            interactionSource = filterTypeInteraction,
                            intensity = PressIntensity.INTENSE
                        ),
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

            if (availableTags.isNotEmpty()) {
                FilterSectionTitle(stringResource(R.string.filter_section_category))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    availableTags.forEach { tag ->
                        val filterTagInteraction = remember(tag.id) { MutableInteractionSource() }
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
                            interactionSource = filterTagInteraction,
                            modifier = Modifier.pressScale(
                                interactionSource = filterTagInteraction,
                                intensity = PressIntensity.INTENSE
                            ),
                            label = { Text(tag.name) },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(tag.icon.icon),
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = if (filters.categoryIds.contains(tag.id)) LocalContentColor.current else Color(
                                        tag.color
                                    )
                                )
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            val clearButtonInteraction = remember { MutableInteractionSource() }
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
                interactionSource = clearButtonInteraction,
                modifier = Modifier
                    .fillMaxWidth()
                    .pressScale(
                        interactionSource = clearButtonInteraction,
                        intensity = PressIntensity.VERY_SUBTLE
                    ),
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

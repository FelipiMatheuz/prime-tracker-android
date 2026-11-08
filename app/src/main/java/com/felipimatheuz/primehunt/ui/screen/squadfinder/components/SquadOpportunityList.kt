package com.felipimatheuz.primehunt.ui.screen.squadfinder.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.domain.model.GoalDomain
import com.felipimatheuz.primehunt.domain.model.enums.AppTheme
import com.felipimatheuz.primehunt.domain.model.squad.Platform
import com.felipimatheuz.primehunt.domain.model.squad.Region
import com.felipimatheuz.primehunt.domain.model.squad.SquadAnnouncement
import com.felipimatheuz.primehunt.ui.screen.components.PrimeSearchBar
import com.felipimatheuz.primehunt.ui.theme.PrimeTrackerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SquadOpportunityListContent(
    publicAnnouncements: List<SquadAnnouncement>,
    availableGoals: List<GoalDomain>,
    selectedTargetIdFilter: String?,
    selectedPlatformFilter: Platform?,
    selectedRegionFilter: Region?,
    isLoading: Boolean,
    onTargetFilterChange: (String?) -> Unit,
    onPlatformFilterChange: (Platform?) -> Unit,
    onRegionFilterChange: (Region?) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var showFilterBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    val activeFiltersCount = (if (selectedPlatformFilter != null) 1 else 0) +
            (if (selectedRegionFilter != null) 1 else 0)

    if (showFilterBottomSheet) {
        SquadFilterBottomSheet(
            selectedPlatformFilter = selectedPlatformFilter,
            selectedRegionFilter = selectedRegionFilter,
            onPlatformFilterChange = onPlatformFilterChange,
            onRegionFilterChange = onRegionFilterChange,
            onClearFilters = {
                onPlatformFilterChange(null)
                onRegionFilterChange(null)
            },
            onDismiss = { showFilterBottomSheet = false },
            sheetState = sheetState
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Search Bar with Filter Action
        PrimeSearchBar(
            query = searchQuery,
            onQueryChange = { query ->
                searchQuery = query
                if (query.isBlank()) {
                    onTargetFilterChange(null)
                }
            },
            placeholderRes = R.string.search_description,
            activeFiltersCount = activeFiltersCount,
            onFilterClick = { showFilterBottomSheet = true },
            onClearClick = {
                searchQuery = ""
                onTargetFilterChange(null)
            },
            modifier = Modifier.fillMaxWidth()
        )

        // Goal Suggestions (if user has active goals)
        if (availableGoals.isNotEmpty()) {
            Text(
                text = stringResource(R.string.squad_list_my_goals_title),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedTargetIdFilter == null,
                        onClick = { onTargetFilterChange(null) },
                        label = { Text(stringResource(R.string.squad_list_filter_all)) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
                items(availableGoals, key = { it.id }) { goal ->
                    val isSelected = selectedTargetIdFilter == goal.targetId
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            if (isSelected) onTargetFilterChange(null)
                            else onTargetFilterChange(goal.targetId)
                        },
                        label = { Text(goal.targetName) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Announcements List / States
        when {
            isLoading -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(4) {
                        SquadCardSkeleton()
                    }
                }
            }

            publicAnnouncements.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_squad),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(R.string.squad_list_empty_title),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.squad_list_empty_desc),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            else -> {
                val filteredList = if (searchQuery.isNotBlank()) {
                    publicAnnouncements.filter {
                        it.targetName.contains(searchQuery, ignoreCase = true) ||
                                it.nickname.contains(searchQuery, ignoreCase = true)
                    }
                } else {
                    publicAnnouncements
                }

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(filteredList, key = { it.id }) { announcement ->
                        SquadCard(announcement = announcement)
                    }
                }
            }
        }
    }
}

@Preview(name = "Squad Opportunity List - Light")
@Composable
fun SquadOpportunityListPreview() {
    PrimeTrackerTheme {
        Surface {
            SquadOpportunityListContent(
                publicAnnouncements = listOf(
                    SquadAnnouncement(
                        id = "1",
                        userId = "1",
                        targetId = "ash_prime",
                        targetName = "Ash Prime Set",
                        nickname = "Tenno1",
                        platform = Platform.PC,
                        region = Region.SOUTH_AMERICA,
                        createdAt = System.currentTimeMillis(),
                        expiresAt = System.currentTimeMillis() + 300000,
                        note = "Radshare 4/4",
                        isMine = false,
                        isExpired = false
                    )
                ),
                availableGoals = emptyList(),
                selectedTargetIdFilter = null,
                selectedPlatformFilter = null,
                selectedRegionFilter = null,
                isLoading = false,
                onTargetFilterChange = {},
                onPlatformFilterChange = {},
                onRegionFilterChange = {}
            )
        }
    }
}

@Preview(name = "Squad Opportunity List - Empty Dark")
@Composable
fun SquadOpportunityListEmptyPreview() {
    PrimeTrackerTheme(theme = AppTheme.DARK) {
        Surface {
            SquadOpportunityListContent(
                publicAnnouncements = emptyList(),
                availableGoals = emptyList(),
                selectedTargetIdFilter = null,
                selectedPlatformFilter = null,
                selectedRegionFilter = null,
                isLoading = false,
                onTargetFilterChange = {},
                onPlatformFilterChange = {},
                onRegionFilterChange = {}
            )
        }
    }
}

package com.felipimatheuz.primehunt.ui.screen.relic.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.domain.model.enums.RelicEra
import com.felipimatheuz.primehunt.domain.model.enums.RelicSource
import com.felipimatheuz.primehunt.domain.model.enums.ProgressFilter
import com.felipimatheuz.primehunt.ui.viewmodel.relic.RelicFilters
import com.felipimatheuz.primehunt.domain.model.enums.RelicsView

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun RelicFilterBottomSheet(
    currentView: RelicsView,
    filters: RelicFilters,
    onFiltersChanged: (RelicFilters) -> Unit,
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

            if (currentView != RelicsView.ERA) {
                FilterSectionTitle(stringResource(R.string.filter_section_era))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RelicEra.entries.forEach { era ->
                        FilterChip(
                            selected = filters.eras.contains(era),
                            onClick = {
                                val newEras = if (filters.eras.contains(era)) {
                                    filters.eras - era
                                } else {
                                    filters.eras + era
                                }
                                onFiltersChanged(filters.copy(eras = newEras))
                            },
                            label = { Text(era.displayName) }
                        )
                    }
                }
            }

            if (currentView != RelicsView.AVAILABILITY) {
                FilterSectionTitle(stringResource(R.string.filter_section_availability))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RelicSource.entries.forEach { source ->
                        FilterChip(
                            selected = filters.availabilities.contains(source),
                            onClick = {
                                val newAvail = if (filters.availabilities.contains(source)) {
                                    filters.availabilities - source
                                } else {
                                    filters.availabilities + source
                                }
                                onFiltersChanged(filters.copy(availabilities = newAvail))
                            },
                            label = { Text(stringResource(source.displayNameRes)) }
                        )
                    }
                }
            }

            if (currentView != RelicsView.PROGRESS) {
                FilterSectionTitle(stringResource(R.string.filter_section_progress))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ProgressFilter.entries.forEach { option ->
                        FilterChip(
                            selected = filters.progress == option,
                            onClick = { onFiltersChanged(filters.copy(progress = option)) },
                            label = { Text(stringResource(option.displayNameRes)) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = { onFiltersChanged(RelicFilters()); onDismiss() },
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

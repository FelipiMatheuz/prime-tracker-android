package com.felipimatheuz.primehunt.ui.screen.primeset.components

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
import com.felipimatheuz.primehunt.data.remote.enums.PrimeType
import com.felipimatheuz.primehunt.data.remote.enums.RelicSource
import com.felipimatheuz.primehunt.ui.viewmodel.primeset.PrimeSetFilters
import com.felipimatheuz.primehunt.ui.viewmodel.primeset.ProgressFilter

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FilterBottomSheet(
    filters: PrimeSetFilters,
    onFiltersChanged: (PrimeSetFilters) -> Unit,
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
            Text(stringResource(R.string.filter_title), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            // Progress Filter
            FilterSectionTitle(stringResource(R.string.filter_section_progress))
            FlowRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ProgressFilter.entries.forEach { option ->
                    FilterChip(
                        selected = filters.progress == option,
                        onClick = { onFiltersChanged(filters.copy(progress = option)) },
                        label = { Text(stringResource(option.displayNameRes)) }
                    )
                }
            }

            // Category Filter
            FilterSectionTitle(stringResource(R.string.filter_section_category))
            FlowRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PrimeType.entries.forEach { type ->
                    FilterChip(
                        selected = filters.categories.contains(type),
                        onClick = {
                            val newCategories = if (filters.categories.contains(type)) {
                                filters.categories - type
                            } else {
                                filters.categories + type
                            }
                            onFiltersChanged(filters.copy(categories = newCategories))
                        },
                        label = { Text(stringResource(type.displayNameRes)) }
                    )
                }
            }

            // Availability Filter
            FilterSectionTitle(stringResource(R.string.filter_section_availability))
            FlowRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
            
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = { onFiltersChanged(PrimeSetFilters()); onDismiss() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.onErrorContainer)
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

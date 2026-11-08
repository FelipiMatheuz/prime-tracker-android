package com.felipimatheuz.primehunt.ui.screen.squadfinder.components

import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.domain.model.squad.Platform
import com.felipimatheuz.primehunt.domain.model.squad.Region
import com.felipimatheuz.primehunt.ui.modifier.PressIntensity
import com.felipimatheuz.primehunt.ui.modifier.pressScale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SquadFilterBottomSheet(
    selectedPlatformFilter: Platform?,
    selectedRegionFilter: Region?,
    onPlatformFilterChange: (Platform?) -> Unit,
    onRegionFilterChange: (Region?) -> Unit,
    onClearFilters: () -> Unit,
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

            // Platform Section
            FilterSectionTitle(stringResource(R.string.squad_profile_platform_label))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val allPlatformInteraction = remember { MutableInteractionSource() }
                FilterChip(
                    selected = selectedPlatformFilter == null,
                    onClick = { onPlatformFilterChange(null) },
                    interactionSource = allPlatformInteraction,
                    label = { Text(stringResource(R.string.squad_list_filter_all)) },
                    modifier = Modifier.pressScale(allPlatformInteraction, PressIntensity.INTENSE)
                )

                Platform.entries.forEach { platform ->
                    val chipInteraction = remember(platform) { MutableInteractionSource() }
                    val isSelected = selectedPlatformFilter == platform
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            if (isSelected) onPlatformFilterChange(null)
                            else onPlatformFilterChange(platform)
                        },
                        interactionSource = chipInteraction,
                        label = { Text(stringResource(platform.labelRes)) },
                        modifier = Modifier.pressScale(chipInteraction, PressIntensity.INTENSE)
                    )
                }
            }

            // Region Section
            FilterSectionTitle(stringResource(R.string.squad_profile_region_label))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val allRegionInteraction = remember { MutableInteractionSource() }
                FilterChip(
                    selected = selectedRegionFilter == null,
                    onClick = { onRegionFilterChange(null) },
                    interactionSource = allRegionInteraction,
                    label = { Text(stringResource(R.string.squad_list_filter_all)) },
                    modifier = Modifier.pressScale(allRegionInteraction, PressIntensity.INTENSE)
                )

                Region.entries.forEach { region ->
                    val chipInteraction = remember(region) { MutableInteractionSource() }
                    val isSelected = selectedRegionFilter == region
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            if (isSelected) onRegionFilterChange(null)
                            else onRegionFilterChange(region)
                        },
                        interactionSource = chipInteraction,
                        label = { Text(stringResource(region.labelRes)) },
                        modifier = Modifier.pressScale(chipInteraction, PressIntensity.INTENSE)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            val clearButtonInteraction = remember { MutableInteractionSource() }
            Button(
                onClick = {
                    onClearFilters()
                    onDismiss()
                },
                interactionSource = clearButtonInteraction,
                modifier = Modifier
                    .fillMaxWidth()
                    .pressScale(clearButtonInteraction, PressIntensity.VERY_SUBTLE),
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

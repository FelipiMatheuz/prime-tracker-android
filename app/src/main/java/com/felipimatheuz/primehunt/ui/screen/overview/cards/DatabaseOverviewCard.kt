package com.felipimatheuz.primehunt.ui.screen.overview.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.ui.screen.overview.orbital.OrbitalDimens
import com.felipimatheuz.primehunt.ui.theme.Completed
import com.felipimatheuz.primehunt.ui.theme.PrimeTrackerTheme
import com.felipimatheuz.primehunt.ui.viewmodel.overview.DatabaseOverviewUi

@Composable
fun DatabaseOverviewCard(state: DatabaseOverviewUi) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(R.string.overview_database),
            style = MaterialTheme.typography.titleMedium.copy(textAlign = TextAlign.Center),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            DatabaseItem(
                label = stringResource(R.string.overview_db_collections),
                value = state.collectionsCount.toString(),
                statusColor = state.collectionsStatusColor
            )
            DatabaseItem(
                label = stringResource(R.string.overview_db_sets),
                value = state.setsCount.toString(),
                statusColor = state.setsStatusColor
            )
            DatabaseItem(
                label = stringResource(R.string.overview_db_parts),
                value = state.partsCount.toString(),
                statusColor = state.setsStatusColor // Sets and Parts share same file
            )
            DatabaseItem(
                label = stringResource(R.string.overview_db_relics),
                value = state.relicsCount.toString(),
                statusColor = state.relicsStatusColor
            )
        }

        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
            Text(
                text = stringResource(R.string.overview_db_last_sync),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = state.lastSync,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun DatabaseItem(label: String, value: String, statusColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(8.dp),
                color = statusColor,
                shape = MaterialTheme.shapes.extraSmall
            ) {}
            Text(text = label, style = MaterialTheme.typography.bodyMedium)
        }
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DatabaseOverviewCardPreview() {
    PrimeTrackerTheme {
        Box(
            modifier = Modifier
                .size(OrbitalDimens.CardSize)
                .padding(48.dp, 48.dp)
        ) {
            DatabaseOverviewCard(
                state = DatabaseOverviewUi(
                    collectionsCount = 10,
                    setsCount = 20,
                    partsCount = 30,
                    relicsCount = 40,
                    lastSync = "10/10/2023 10:10",
                    collectionsStatusColor = Completed,
                    setsStatusColor = Completed,
                    relicsStatusColor = Completed
                )
            )
        }
    }
}

package com.felipimatheuz.primehunt.ui.screen.overview.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import com.felipimatheuz.primehunt.ui.theme.Vault
import com.felipimatheuz.primehunt.ui.theme.PrimeTrackerTheme
import com.felipimatheuz.primehunt.ui.viewmodel.overview.RelicsOverviewUi

@Composable
fun RelicsOverviewCard(state: RelicsOverviewUi) {
    Column(
        modifier = Modifier
            .fillMaxSize().padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.SpaceAround
    ) {
        Text(
            text = stringResource(R.string.menu_relics),
            style = MaterialTheme.typography.titleLarge.copy(textAlign = TextAlign.Center),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            val totalCount = state.available + state.vaulted + state.resurgence + state.baro

            RelicItem(
                stringResource(R.string.status_mission),
                state.available,
                totalCount,
                MaterialTheme.colorScheme.onSurfaceVariant
            )
            RelicItem(
                stringResource(R.string.status_vault),
                state.vaulted,
                totalCount,
                Vault
            )
            RelicItem(
                stringResource(R.string.status_resurgence),
                state.resurgence,
                totalCount,
                MaterialTheme.colorScheme.primary
            )
            RelicItem(
                stringResource(R.string.status_baro),
                state.baro,
                totalCount,
                MaterialTheme.colorScheme.primaryContainer
            )
        }
    }
}

@Composable
private fun RelicItem(label: String, count: Int, totalCount: Int, color: Color) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            LinearProgressIndicator(
                progress = { count / (totalCount + 1f) },
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .padding(horizontal = 4.dp),
                color = color,
                trackColor = MaterialTheme.colorScheme.surface
            )
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RelicsOverviewCardPreview() {
    PrimeTrackerTheme {
        Box(
            modifier = Modifier
                .size(OrbitalDimens.CardSize)
                .padding(48.dp, 48.dp)
        ) {
            RelicsOverviewCard(
                state = RelicsOverviewUi(
                    available = 10,
                    vaulted = 20,
                    resurgence = 30,
                    baro = 40
                )
            )
        }
    }
}
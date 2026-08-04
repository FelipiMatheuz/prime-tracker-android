package com.felipimatheuz.primehunt.ui.screen.overview.cards

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.ui.screen.overview.orbital.OrbitalDimens
import com.felipimatheuz.primehunt.ui.theme.PrimeTrackerTheme
import com.felipimatheuz.primehunt.ui.viewmodel.overview.TradeOverviewUi

@Composable
fun TradeOverviewCard(state: TradeOverviewUi) {
    Column(
        modifier = Modifier
            .fillMaxSize().padding(vertical = 8.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(R.drawable.regal_aya),
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = stringResource(R.string.overview_trade_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }


        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            TradeItem(
                stringResource(R.string.overview_trade_sets),
                state.duplicateSets.toString(),
                stringResource(R.string.overview_trade_unit_sets)
            )
            TradeItem(
                stringResource(R.string.overview_trade_parts),
                state.duplicateParts.toString(),
                stringResource(R.string.overview_trade_unit_parts)
            )
        }

        Text(
            text = stringResource(R.string.overview_trade_ducats, state.averageDucatPrice),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun TradeItem(label: String, value: String, unit: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = " $unit",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TradeOverviewCardPreview() {
    PrimeTrackerTheme {
        Box(
            modifier = Modifier
                .size(OrbitalDimens.CardSize)
                .padding(48.dp, 48.dp)
        ) {
            TradeOverviewCard(
                state = TradeOverviewUi(
                    duplicateSets = 10,
                    duplicateParts = 20
                )
            )
        }
    }
}

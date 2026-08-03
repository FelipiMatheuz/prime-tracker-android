package com.felipimatheuz.primehunt.ui.screen.overview.cards

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.felipimatheuz.primehunt.ui.viewmodel.overview.TradeOverviewUi

@Composable
fun TradeOverviewCard(state: TradeOverviewUi) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "TRADE",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            TradeItem("Duplicate Sets", state.duplicateSets.toString(), "SETS")
            TradeItem("Duplicate Parts", state.duplicateParts.toString(), "PARTS")
        }

        Text(
            text = "Future module features will appear here",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun TradeItem(label: String, value: String, unit: String) {
    Column {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(verticalAlignment = Alignment.Bottom) {
            Text(text = value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(
                text = " $unit",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }
    }
}

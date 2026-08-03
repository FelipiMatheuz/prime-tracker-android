package com.felipimatheuz.primehunt.ui.screen.overview.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.felipimatheuz.primehunt.ui.viewmodel.overview.RelicsOverviewUi

@Composable
fun RelicsOverviewCard(state: RelicsOverviewUi) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "RELICS",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            RelicItem("Available", state.available, MaterialTheme.colorScheme.primary)
            RelicItem("Vaulted", state.vaulted, Color.Gray)
            RelicItem("Resurgence", state.resurgence, Color(0xFFDAA520))
            RelicItem("Baro", state.baro, Color(0xFF4169E1))
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun RelicItem(label: String, count: Int, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Surface(modifier = Modifier.size(12.dp), color = color, shape = MaterialTheme.shapes.extraSmall) {}
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
        Text(text = count.toString(), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    }
}
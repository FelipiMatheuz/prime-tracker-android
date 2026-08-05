package com.felipimatheuz.primehunt.ui.screen.cloud.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.felipimatheuz.primehunt.ui.viewmodel.cloud.CloudState

@Composable
fun LegacyMigrationCard(
    state: CloudState,
    onMigrateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isEnabled = state.isAuthenticated && !state.isLoading && !state.isMigrationSuccess
    val alpha = if (state.isAuthenticated) 1f else 0.5f

    Card(
        modifier = modifier
            .fillMaxWidth()
            .alpha(alpha),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Legacy Migration",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Import your old checklist into the new Inventory system. (Temporary feature)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onMigrateClick,
                enabled = isEnabled,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (state.isLoading && !state.isMigrationSuccess && state.isAuthenticated) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(if (state.isMigrationSuccess) "Migration Completed" else "Start Migration")
                }
            }
        }
    }
}

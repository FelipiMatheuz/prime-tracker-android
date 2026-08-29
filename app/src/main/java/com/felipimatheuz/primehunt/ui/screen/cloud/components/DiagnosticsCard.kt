package com.felipimatheuz.primehunt.ui.screen.cloud.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.ui.modifier.PressIntensity
import com.felipimatheuz.primehunt.ui.modifier.pressScale
import com.felipimatheuz.primehunt.ui.viewmodel.cloud.CloudState

@Composable
fun DiagnosticsCard(
    state: CloudState,
    onSendLogsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
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
                text = stringResource(R.string.cloud_diagnostics_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(R.string.cloud_diagnostics_desc),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            val logButtonInteraction = remember { MutableInteractionSource() }
            OutlinedButton(
                onClick = onSendLogsClick,
                interactionSource = logButtonInteraction,
                enabled = state.isAuthenticated,
                modifier = Modifier
                    .fillMaxWidth()
                    .pressScale(
                        interactionSource = logButtonInteraction,
                        PressIntensity.VERY_SUBTLE,
                        state.isAuthenticated
                    )
            ) {
                Text(stringResource(R.string.cloud_log_debug))
            }
        }
    }
}

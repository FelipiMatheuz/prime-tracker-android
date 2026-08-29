package com.felipimatheuz.primehunt.ui.screen.cloud.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
fun DangerZoneCard(
    state: CloudState,
    onClearCloudDataClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showConfirmation by remember { mutableStateOf(false) }
    val isEnabled = state.isAuthenticated && !state.isLoading
    val alpha = if (state.isAuthenticated) 1f else 0.5f

    Card(
        modifier = modifier
            .fillMaxWidth()
            .alpha(alpha),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.cloud_danger_zone_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error
            )
            Text(
                text = stringResource(R.string.cloud_danger_zone_desc),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(16.dp))
            val clearButtonInteraction = remember { MutableInteractionSource() }
            Button(
                interactionSource = clearButtonInteraction,
                onClick = { showConfirmation = true },
                enabled = isEnabled,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .pressScale(
                        interactionSource = clearButtonInteraction,
                        intensity = PressIntensity.VERY_SUBTLE,
                        enabled = isEnabled
                    )
            ) {
                if (state.isLoading && state.isAuthenticated) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onError
                    )
                } else {
                    Text(stringResource(R.string.cloud_danger_zone_clear))
                }
            }
        }
    }

    if (showConfirmation) {
        AlertDialog(
            onDismissRequest = { showConfirmation = false },
            title = { Text(stringResource(R.string.cloud_danger_zone_dialog)) },
            text = { Text(stringResource(R.string.cloud_danger_zone_description)) },
            confirmButton = {
                val clearButtonInteraction = remember { MutableInteractionSource() }
                TextButton(
                    onClick = {
                        showConfirmation = false
                        onClearCloudDataClick()
                    },
                    interactionSource = clearButtonInteraction,
                    modifier = Modifier.pressScale(
                        interactionSource = clearButtonInteraction,
                        intensity = PressIntensity.VERY_SUBTLE
                    )
                ) {
                    Text(
                        stringResource(R.string.cloud_danger_zone_delete),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                val cancelButtonInteraction = remember { MutableInteractionSource() }
                TextButton(
                    onClick = { showConfirmation = false },
                    interactionSource = cancelButtonInteraction,
                    modifier = Modifier.pressScale(
                        interactionSource = cancelButtonInteraction,
                        intensity = PressIntensity.VERY_SUBTLE
                    )
                ) {
                    Text(stringResource(R.string.cloud_danger_zone_cancel))
                }
            }
        )
    }
}

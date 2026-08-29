package com.felipimatheuz.primehunt.ui.screen.primeset.primedetail.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.ui.modifier.PressIntensity
import com.felipimatheuz.primehunt.ui.modifier.pressScale

@Composable
fun DetailQuickActions(
    onAddSet: () -> Unit,
    onRemoveSet: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        val plusOneInteraction = remember { MutableInteractionSource() }
        val minusOneInteraction = remember { MutableInteractionSource() }

        Button(
            onClick = onRemoveSet,
            interactionSource = minusOneInteraction,
            modifier = Modifier
                .weight(1f)
                .pressScale(minusOneInteraction, PressIntensity.VERY_SUBTLE),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        ) {
            Text(stringResource(R.string.detail_action_remove_set))
        }
        Button(
            onClick = onAddSet,
            interactionSource = plusOneInteraction,
            modifier = Modifier
                .weight(1f)
                .pressScale(plusOneInteraction, PressIntensity.VERY_SUBTLE)
        ) {
            Text(stringResource(R.string.detail_action_add_set))
        }
    }
}

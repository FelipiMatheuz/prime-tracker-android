package com.felipimatheuz.primehunt.ui.screen.squadfinder.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.domain.model.enums.AppTheme
import com.felipimatheuz.primehunt.ui.modifier.PressIntensity
import com.felipimatheuz.primehunt.ui.modifier.pressScale
import com.felipimatheuz.primehunt.ui.theme.PrimeTrackerTheme

@Composable
fun ProfileRequiredCard(
    onNavigateToProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.squad_my_profile_required_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.squad_my_profile_required_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))

            val profileButtonInteraction = remember { MutableInteractionSource() }
            Button(
                onClick = onNavigateToProfile,
                modifier = Modifier.pressScale(
                    profileButtonInteraction,
                    PressIntensity.MODERATE
                )
            ) {
                Text(stringResource(R.string.squad_my_profile_setup_button))
            }
        }
    }
}

@Preview(name = "Profile Required Card - Light")
@Composable
fun ProfileRequiredCardPreview() {
    PrimeTrackerTheme {
        Surface {
            ProfileRequiredCard(onNavigateToProfile = {})
        }
    }
}

@Preview(name = "Profile Required Card - Dark")
@Composable
fun ProfileRequiredCardDarkPreview() {
    PrimeTrackerTheme(theme = AppTheme.DARK) {
        Surface {
            ProfileRequiredCard(onNavigateToProfile = {})
        }
    }
}

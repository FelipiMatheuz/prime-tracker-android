package com.felipimatheuz.primehunt.ui.screen.squadfinder.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.domain.model.enums.AppTheme
import com.felipimatheuz.primehunt.domain.model.squad.Platform
import com.felipimatheuz.primehunt.domain.model.squad.Region
import com.felipimatheuz.primehunt.domain.model.squad.UserProfile
import com.felipimatheuz.primehunt.ui.modifier.PressIntensity
import com.felipimatheuz.primehunt.ui.modifier.pressScale
import com.felipimatheuz.primehunt.ui.theme.PrimeTrackerTheme

@Composable
fun ProfileDataCard(
    userProfile: UserProfile,
    onNavigateToProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(R.string.squad_my_inherited_profile_title),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${stringResource(R.string.squad_card_nick_label)} ${userProfile.nickname}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${stringResource(R.string.squad_profile_platform_label)}: ${
                        stringResource(
                            userProfile.platform.labelRes
                        )
                    }",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "${stringResource(R.string.squad_profile_region_label)}: ${
                        stringResource(
                            userProfile.region.labelRes
                        )
                    }",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            val editChipInteraction = remember { MutableInteractionSource() }
            AssistChip(
                onClick = onNavigateToProfile,
                label = {
                    Text(stringResource(R.string.squad_my_inherited_profile_edit))
                },
                interactionSource = editChipInteraction,
                modifier = Modifier.pressScale(
                    interactionSource = editChipInteraction,
                    intensity = PressIntensity.VERY_SUBTLE
                )
            )
        }
    }
}

@Preview(name = "Profile Data Card - Light")
@Composable
fun ProfileDataCardPreview() {
    PrimeTrackerTheme {
        Surface {
            ProfileDataCard(
                userProfile = UserProfile("1", "TennoPrime", Platform.PC, Region.SOUTH_AMERICA),
                onNavigateToProfile = {}
            )
        }
    }
}

@Preview(name = "Profile Data Card - Dark")
@Composable
fun ProfileDataCardDarkPreview() {
    PrimeTrackerTheme(theme = AppTheme.DARK) {
        Surface {
            ProfileDataCard(
                userProfile = UserProfile("1", "TennoPrime", Platform.PC, Region.SOUTH_AMERICA),
                onNavigateToProfile = {}
            )
        }
    }
}

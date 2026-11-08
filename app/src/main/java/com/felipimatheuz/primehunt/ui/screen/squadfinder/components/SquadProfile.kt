package com.felipimatheuz.primehunt.ui.screen.squadfinder.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
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
import com.felipimatheuz.primehunt.ui.screen.components.PrimePanel
import com.felipimatheuz.primehunt.ui.theme.PrimeTrackerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SquadProfileContent(
    userProfile: UserProfile?,
    onSaveProfile: (nickname: String, platform: Platform, region: Region) -> Unit,
    modifier: Modifier = Modifier
) {
    var nickname by remember(userProfile) { mutableStateOf(userProfile?.nickname ?: "") }
    var selectedPlatform by remember(userProfile) { mutableStateOf(userProfile?.platform ?: Platform.PC) }
    var selectedRegion by remember(userProfile) { mutableStateOf(userProfile?.region ?: Region.NORTH_AMERICA) }

    var platformDropdownExpanded by remember { mutableStateOf(false) }
    var regionDropdownExpanded by remember { mutableStateOf(false) }

    val isNicknameValid = nickname.isNotBlank() && nickname.length <= 30
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Text(
            text = stringResource(R.string.squad_profile_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = stringResource(R.string.squad_profile_desc),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = nickname,
            onValueChange = { if (it.length <= 30) nickname = it },
            label = { Text(stringResource(R.string.squad_profile_nickname_label)) },
            placeholder = { Text(stringResource(R.string.squad_profile_nickname_placeholder)) },
            isError = nickname.isNotBlank() && nickname.length > 30,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        ExposedDropdownMenuBox(
            expanded = platformDropdownExpanded,
            onExpandedChange = { platformDropdownExpanded = !platformDropdownExpanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = stringResource(selectedPlatform.labelRes),
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.squad_profile_platform_label)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = platformDropdownExpanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = platformDropdownExpanded,
                onDismissRequest = { platformDropdownExpanded = false }
            ) {
                Platform.entries.forEach { platform ->
                    DropdownMenuItem(
                        text = { Text(stringResource(platform.labelRes)) },
                        onClick = {
                            selectedPlatform = platform
                            platformDropdownExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        ExposedDropdownMenuBox(
            expanded = regionDropdownExpanded,
            onExpandedChange = { regionDropdownExpanded = !regionDropdownExpanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = stringResource(selectedRegion.labelRes),
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.squad_profile_region_label)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = regionDropdownExpanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = regionDropdownExpanded,
                onDismissRequest = { regionDropdownExpanded = false }
            ) {
                Region.entries.forEach { region ->
                    DropdownMenuItem(
                        text = { Text(stringResource(region.labelRes)) },
                        onClick = {
                            selectedRegion = region
                            regionDropdownExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        val saveButtonInteraction = remember { MutableInteractionSource() }
        Button(
            onClick = {
                if (isNicknameValid) {
                    onSaveProfile(nickname.trim(), selectedPlatform, selectedRegion)
                }
            },
            enabled = isNicknameValid,
            modifier = Modifier
                .fillMaxWidth()
                .pressScale(saveButtonInteraction, PressIntensity.MODERATE)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_check),
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.squad_profile_save_button),
                fontWeight = FontWeight.Bold
            )
        }

        if (userProfile != null) {
            Spacer(modifier = Modifier.height(24.dp))
            PrimePanel(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = stringResource(R.string.squad_profile_saved_title),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${stringResource(R.string.squad_card_nick_label)}${userProfile.nickname}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${stringResource(R.string.squad_profile_platform_label)}: ${stringResource(userProfile.platform.labelRes)}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "${stringResource(R.string.squad_profile_region_label)}: ${stringResource(userProfile.region.labelRes)}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Preview(name = "Squad Profile - Light")
@Composable
fun SquadProfilePreview() {
    PrimeTrackerTheme {
        Surface {
            SquadProfileContent(
                userProfile = UserProfile("1", "TennoPrime", Platform.PC, Region.SOUTH_AMERICA),
                onSaveProfile = { _, _, _ -> }
            )
        }
    }
}

@Preview(name = "Squad Profile - Dark")
@Composable
fun SquadProfileDarkPreview() {
    PrimeTrackerTheme(theme = AppTheme.DARK) {
        Surface {
            SquadProfileContent(
                userProfile = null,
                onSaveProfile = { _, _, _ -> }
            )
        }
    }
}

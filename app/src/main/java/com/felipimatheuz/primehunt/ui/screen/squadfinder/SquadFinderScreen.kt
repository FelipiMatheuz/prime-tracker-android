package com.felipimatheuz.primehunt.ui.screen.squadfinder

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.domain.model.enums.AppTheme
import com.felipimatheuz.primehunt.ui.screen.components.PrimeSegmentedSelector
import com.felipimatheuz.primehunt.ui.screen.squadfinder.components.SquadMyAnnouncementContent
import com.felipimatheuz.primehunt.ui.screen.squadfinder.components.SquadOpportunityListContent
import com.felipimatheuz.primehunt.ui.screen.squadfinder.components.SquadProfileContent
import com.felipimatheuz.primehunt.ui.theme.PrimeTrackerTheme
import com.felipimatheuz.primehunt.ui.viewmodel.squadfinder.SquadFinderIntent
import com.felipimatheuz.primehunt.ui.viewmodel.squadfinder.SquadFinderViewModel

enum class SquadTab(@StringRes val titleRes: Int) {
    OPPORTUNITIES(R.string.squad_tab_opportunities),
    MY_ANNOUNCEMENT(R.string.squad_tab_my_announcement),
    PROFILE(R.string.squad_tab_profile)
}

@Composable
fun SquadFinderScreen(
    paddingValues: PaddingValues,
    viewModel: SquadFinderViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var selectedTab by rememberSaveable { mutableStateOf(SquadTab.OPPORTUNITIES) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.onIntent(SquadFinderIntent.ClearError)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Offline Warning Banner
            AnimatedVisibility(visible = !state.isConnected) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.errorContainer)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_info),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.squad_screen_offline_banner),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Segmented Selector Header
            PrimeSegmentedSelector(
                options = SquadTab.entries,
                selectedOption = selectedTab,
                onOptionClick = { selectedTab = it },
                labelProvider = { it.titleRes },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Content according to selected tab
            when (selectedTab) {
                SquadTab.OPPORTUNITIES -> SquadOpportunityListContent(
                    publicAnnouncements = state.publicAnnouncements,
                    availableGoals = state.availableGoals,
                    selectedTargetIdFilter = state.targetIdFilter,
                    selectedPlatformFilter = state.platformFilter,
                    selectedRegionFilter = state.regionFilter,
                    isLoading = state.isLoading,
                    onTargetFilterChange = { id -> viewModel.onIntent(SquadFinderIntent.SetTargetFilter(id)) },
                    onPlatformFilterChange = { p -> viewModel.onIntent(SquadFinderIntent.SetPlatformFilter(p)) },
                    onRegionFilterChange = { r -> viewModel.onIntent(SquadFinderIntent.SetRegionFilter(r)) }
                )

                SquadTab.MY_ANNOUNCEMENT -> SquadMyAnnouncementContent(
                    userProfile = state.userProfile,
                    ownAnnouncement = state.ownAnnouncement,
                    availableGoals = state.availableGoals,
                    isConnected = state.isConnected,
                    onCreateAnnouncement = { targetId, note ->
                        viewModel.onIntent(SquadFinderIntent.CreateAnnouncement(targetId, note))
                    },
                    onStopAnnouncement = {
                        viewModel.onIntent(SquadFinderIntent.DeleteAnnouncement)
                    },
                    onNavigateToProfile = { selectedTab = SquadTab.PROFILE }
                )

                SquadTab.PROFILE -> SquadProfileContent(
                    userProfile = state.userProfile,
                    onSaveProfile = { nickname, platform, region ->
                        viewModel.onIntent(SquadFinderIntent.SaveProfile(nickname, platform, region))
                    }
                )
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }
}

@Preview(name = "Squad Finder Screen - Light")
@Composable
fun SquadFinderScreenPreview() {
    PrimeTrackerTheme {
        Surface {
            SquadFinderScreen(paddingValues = PaddingValues(0.dp))
        }
    }
}

@Preview(name = "Squad Finder Screen - Dark")
@Composable
fun SquadFinderScreenDarkPreview() {
    PrimeTrackerTheme(theme = AppTheme.DARK) {
        Surface {
            SquadFinderScreen(paddingValues = PaddingValues(0.dp))
        }
    }
}

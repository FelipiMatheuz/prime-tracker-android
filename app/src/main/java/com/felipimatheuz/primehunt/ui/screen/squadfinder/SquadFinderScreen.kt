package com.felipimatheuz.primehunt.ui.screen.squadfinder

import androidx.activity.compose.BackHandler
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
import com.felipimatheuz.primehunt.ui.screen.squadfinder.components.SquadActiveAnnouncementContent
import com.felipimatheuz.primehunt.ui.screen.squadfinder.components.SquadCreateAnnouncementContent
import com.felipimatheuz.primehunt.ui.screen.squadfinder.components.SquadOpportunityListContent
import com.felipimatheuz.primehunt.ui.screen.squadfinder.components.SquadProfileContent
import com.felipimatheuz.primehunt.ui.theme.PrimeTrackerTheme
import com.felipimatheuz.primehunt.ui.viewmodel.squadfinder.SquadFinderIntent
import com.felipimatheuz.primehunt.ui.viewmodel.squadfinder.SquadFinderViewModel

enum class SquadSubScreen {
    LIST,
    CREATE,
    PROFILE
}

@Composable
fun SquadFinderScreen(
    paddingValues: PaddingValues,
    viewModel: SquadFinderViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var currentSubScreen by rememberSaveable { mutableStateOf(SquadSubScreen.LIST) }
    var previousSubScreen by rememberSaveable { mutableStateOf<SquadSubScreen?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.onIntent(SquadFinderIntent.ClearError)
        }
    }

    if (state.ownAnnouncement == null && currentSubScreen != SquadSubScreen.LIST) {
        BackHandler {
            currentSubScreen = previousSubScreen ?: SquadSubScreen.LIST
            previousSubScreen = SquadSubScreen.LIST
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

            val ownAnnouncement = state.ownAnnouncement
            if (ownAnnouncement != null) {
                SquadActiveAnnouncementContent(
                    ownAnnouncement = ownAnnouncement,
                    isConnected = state.isConnected,
                    onStopAnnouncement = {
                        viewModel.onIntent(SquadFinderIntent.DeleteAnnouncement)
                        currentSubScreen = SquadSubScreen.LIST
                    }
                )
            } else {
                when (currentSubScreen) {
                    SquadSubScreen.LIST -> SquadOpportunityListContent(
                        publicAnnouncements = state.publicAnnouncements,
                        availableGoals = state.availableGoals,
                        selectedTargetIdFilter = state.targetIdFilter,
                        selectedPlatformFilter = state.platformFilter,
                        selectedRegionFilter = state.regionFilter,
                        isLoading = state.isLoading,
                        onTargetFilterChange = { id -> viewModel.onIntent(SquadFinderIntent.SetTargetFilter(id)) },
                        onPlatformFilterChange = { p -> viewModel.onIntent(SquadFinderIntent.SetPlatformFilter(p)) },
                        onRegionFilterChange = { r -> viewModel.onIntent(SquadFinderIntent.SetRegionFilter(r)) },
                        onNavigateToCreateAnnouncement = {
                            previousSubScreen = SquadSubScreen.LIST
                            currentSubScreen = SquadSubScreen.CREATE
                        }
                    )

                    SquadSubScreen.CREATE -> SquadCreateAnnouncementContent(
                        userProfile = state.userProfile,
                        availableGoals = state.availableGoals,
                        isConnected = state.isConnected,
                        onCreateAnnouncement = { targetId, note ->
                            viewModel.onIntent(SquadFinderIntent.CreateAnnouncement(targetId, note))
                        },
                        onNavigateToProfile = {
                            previousSubScreen = SquadSubScreen.CREATE
                            currentSubScreen = SquadSubScreen.PROFILE
                        },
                        onClose = {
                            currentSubScreen = SquadSubScreen.LIST
                        }
                    )

                    SquadSubScreen.PROFILE -> SquadProfileContent(
                        userProfile = state.userProfile,
                        onSaveProfile = { nickname, platform, region ->
                            viewModel.onIntent(SquadFinderIntent.SaveProfile(nickname, platform, region))
                            currentSubScreen = SquadSubScreen.CREATE
                        },
                        onClose = {
                            currentSubScreen = previousSubScreen ?: SquadSubScreen.CREATE
                            previousSubScreen = SquadSubScreen.LIST
                        }
                    )
                }
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

package com.felipimatheuz.primehunt.ui.viewmodel.squadfinder

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felipimatheuz.primehunt.domain.model.enums.GoalStatus
import com.felipimatheuz.primehunt.domain.model.squad.Platform
import com.felipimatheuz.primehunt.domain.model.squad.Region
import com.felipimatheuz.primehunt.domain.repository.GoalRepository
import com.felipimatheuz.primehunt.domain.repository.SquadRepository
import com.felipimatheuz.primehunt.service.squad.SquadServiceController
import com.felipimatheuz.primehunt.ui.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SquadFinderViewModel @Inject constructor(
    private val squadRepository: SquadRepository,
    private val goalRepository: GoalRepository,
    @ApplicationContext private val context: Context
) : ViewModel(), MviViewModel<SquadFinderState, SquadFinderIntent> {

    private val _state = MutableStateFlow(SquadFinderState())
    override val state: StateFlow<SquadFinderState> = _state.asStateFlow()

    private var announcementsJob: Job? = null

    init {
        observeConnectionState()
        loadUserProfile()
        observeOwnAnnouncement()
        observeAvailableGoals()
        observePublicAnnouncements()
    }

    override fun onIntent(intent: SquadFinderIntent) {
        when (intent) {
            is SquadFinderIntent.RefreshData -> {
                loadUserProfile()
                observePublicAnnouncements()
            }
            is SquadFinderIntent.SaveProfile -> saveProfile(intent.nickname, intent.platform, intent.region)
            is SquadFinderIntent.CreateAnnouncement -> createAnnouncement(intent.targetId, intent.note)
            is SquadFinderIntent.HeartbeatAnnouncement -> heartbeatAnnouncement()
            is SquadFinderIntent.DeleteAnnouncement -> deleteAnnouncement()
            is SquadFinderIntent.SetTargetFilter -> setTargetFilter(intent.targetId)
            is SquadFinderIntent.SetPlatformFilter -> setPlatformFilter(intent.platform)
            is SquadFinderIntent.SetRegionFilter -> setRegionFilter(intent.region)
            is SquadFinderIntent.ClearError -> _state.update { it.copy(errorMessage = null) }
        }
    }

    private fun observeConnectionState() {
        viewModelScope.launch {
            squadRepository.observeConnectionState()
                .catch { _state.update { it.copy(isConnected = false) } }
                .collect { isConnected ->
                    _state.update { it.copy(isConnected = isConnected) }
                }
        }
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = squadRepository.getProfile()
            result.onSuccess { profile ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        userProfile = profile,
                        regionFilter = it.regionFilter ?: profile?.region
                    )
                }
            }.onFailure { error ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.message
                    )
                }
            }
        }
    }

    private fun observeOwnAnnouncement() {
        viewModelScope.launch {
            squadRepository.observeOwnAnnouncement()
                .catch { e -> _state.update { it.copy(errorMessage = e.message) } }
                .collect { own ->
                    _state.update { it.copy(ownAnnouncement = own) }
                }
        }
    }

    private fun observeAvailableGoals() {
        viewModelScope.launch {
            goalRepository.observeAllWithTags()
                .catch { }
                .collect { goals ->
                    val activeGoals = goals.filter { it.status == GoalStatus.ACTIVE }
                    _state.update { it.copy(availableGoals = activeGoals) }
                }
        }
    }

    private fun observePublicAnnouncements() {
        announcementsJob?.cancel()
        announcementsJob = viewModelScope.launch {
            squadRepository.observePublicAnnouncements(
                targetIdFilter = _state.value.targetIdFilter,
                platformFilter = _state.value.platformFilter,
                regionFilter = _state.value.regionFilter
            )
                .catch { e -> _state.update { it.copy(errorMessage = e.message) } }
                .collect { list ->
                    _state.update { it.copy(publicAnnouncements = list) }
                }
        }
    }

    private fun saveProfile(nickname: String, platform: Platform, region: Region) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = squadRepository.saveProfile(nickname, platform, region)
            result.onSuccess { profile ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        userProfile = profile,
                        regionFilter = it.regionFilter ?: profile.region
                    )
                }
                observePublicAnnouncements()
            }.onFailure { error ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.message
                    )
                }
            }
        }
    }

    private fun createAnnouncement(targetId: String, note: String?) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = squadRepository.createAnnouncement(targetId, note)
            result.onSuccess { announcement ->
                _state.update { it.copy(isLoading = false) }
                SquadServiceController.startSession(
                    context = context,
                    targetId = announcement.targetId,
                    targetName = announcement.targetName,
                    platform = announcement.platform,
                    region = announcement.region
                )
            }.onFailure { error ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.message
                    )
                }
            }
        }
    }

    private fun heartbeatAnnouncement() {
        viewModelScope.launch {
            squadRepository.heartbeatAnnouncement().onFailure { error ->
                _state.update { it.copy(errorMessage = error.message) }
            }
        }
    }

    private fun deleteAnnouncement() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            SquadServiceController.stopSession(context)
            val result = squadRepository.deleteAnnouncement()
            result.onSuccess {
                _state.update { it.copy(isLoading = false, ownAnnouncement = null) }
            }.onFailure { error ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.message
                    )
                }
            }
        }
    }

    private fun setTargetFilter(targetId: String?) {
        _state.update { it.copy(targetIdFilter = targetId) }
        observePublicAnnouncements()
    }

    private fun setPlatformFilter(platform: Platform?) {
        _state.update { it.copy(platformFilter = platform) }
        observePublicAnnouncements()
    }

    private fun setRegionFilter(region: Region?) {
        _state.update { it.copy(regionFilter = region) }
        observePublicAnnouncements()
    }
}

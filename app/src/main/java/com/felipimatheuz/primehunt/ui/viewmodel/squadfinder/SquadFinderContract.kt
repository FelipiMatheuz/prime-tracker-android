package com.felipimatheuz.primehunt.ui.viewmodel.squadfinder

import com.felipimatheuz.primehunt.domain.model.GoalDomain
import com.felipimatheuz.primehunt.domain.model.squad.Platform
import com.felipimatheuz.primehunt.domain.model.squad.Region
import com.felipimatheuz.primehunt.domain.model.squad.SquadAnnouncement
import com.felipimatheuz.primehunt.domain.model.squad.UserProfile
import com.felipimatheuz.primehunt.ui.mvi.MviIntent
import com.felipimatheuz.primehunt.ui.mvi.MviState

data class SquadFinderState(
    val isConnected: Boolean = true,
    val isLoading: Boolean = false,
    val userProfile: UserProfile? = null,
    val ownAnnouncement: SquadAnnouncement? = null,
    val publicAnnouncements: List<SquadAnnouncement> = emptyList(),
    val availableGoals: List<GoalDomain> = emptyList(),
    val targetIdFilter: String? = null,
    val platformFilter: Platform? = null,
    val regionFilter: Region? = null,
    val errorMessage: String? = null
) : MviState

sealed class SquadFinderIntent : MviIntent {
    data object RefreshData : SquadFinderIntent()
    data class SaveProfile(
        val nickname: String,
        val platform: Platform,
        val region: Region
    ) : SquadFinderIntent()
    data class CreateAnnouncement(
        val targetId: String,
        val note: String? = null
    ) : SquadFinderIntent()
    data object HeartbeatAnnouncement : SquadFinderIntent()
    data object DeleteAnnouncement : SquadFinderIntent()
    data class SetTargetFilter(val targetId: String?) : SquadFinderIntent()
    data class SetPlatformFilter(val platform: Platform?) : SquadFinderIntent()
    data class SetRegionFilter(val region: Region?) : SquadFinderIntent()
    data object ClearError : SquadFinderIntent()
}

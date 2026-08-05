package com.felipimatheuz.primehunt.ui.viewmodel.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.data.local.entity.GoalTagEntity
import com.felipimatheuz.primehunt.data.remote.enums.PrimeType
import com.felipimatheuz.primehunt.domain.model.OverviewDomainModel
import com.felipimatheuz.primehunt.domain.usecase.overview.GetOverviewUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class OverviewViewModel @Inject constructor(
    getOverviewUseCase: GetOverviewUseCase
) : ViewModel() {

    val uiState: StateFlow<OverviewState> = getOverviewUseCase()
        .map { it.toUiState() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = OverviewState()
        )

    private fun OverviewDomainModel.toUiState(): OverviewState {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

        return OverviewState(
            primeSets = PrimeSetsOverviewUi(
                progress = primeSets.progress,
                completedSets = primeSets.completedSets,
                inProgressSets = primeSets.inProgressSets,
                missingSets = primeSets.missingSets,
                categories = primeSets.categories.map {
                    CategoryOverviewUi(
                        nameRes = if (it.type == PrimeType.COMPANION) R.string.overview_others else it.type.displayNameRes,
                        percentage = it.percentage
                    )
                }
            ),
            database = DatabaseOverviewUi(
                collectionsCount = database.collectionsCount,
                setsCount = database.setsCount,
                partsCount = database.partsCount,
                relicsCount = database.relicsCount,
                lastSync = database.lastSyncTimestamp?.let { dateFormat.format(Date(it)) } ?: "—"
            ),
            relics = RelicsOverviewUi(
                available = relics.available,
                vaulted = relics.vaulted,
                resurgence = relics.resurgence,
                baro = relics.baro
            ),
            goals = GoalsOverviewUi(
                activeGoals = goals.activeGoals,
                completedGoals = goals.completedGoals,
                mainTag = goals.mainTag?.let {
                    GoalTagEntity(it.id, it.name, it.icon, it.color)
                }
            ),
            trade = TradeOverviewUi(
                duplicateSets = trade.duplicateSets,
                duplicateParts = trade.duplicateParts,
                averageDucatPrice = trade.averageDucatPrice
            )
        )
    }
}

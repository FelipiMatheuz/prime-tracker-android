package com.felipimatheuz.primehunt.ui.viewmodel.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felipimatheuz.primehunt.domain.usecase.overview.GetOverviewUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class OverviewViewModel @Inject constructor(
    getOverviewUseCase: GetOverviewUseCase
) : ViewModel() {

    val uiState: StateFlow<OverviewState> = getOverviewUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = OverviewState()
        )
}

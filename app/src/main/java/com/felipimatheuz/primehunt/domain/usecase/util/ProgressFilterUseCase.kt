package com.felipimatheuz.primehunt.domain.usecase.util

import com.felipimatheuz.primehunt.domain.model.ProgressState
import com.felipimatheuz.primehunt.data.local.enums.ProgressFilter
import javax.inject.Inject

class ProgressFilterUseCase @Inject constructor() {
    
    fun matches(item: ProgressState, filter: ProgressFilter): Boolean {
        return when (filter) {
            ProgressFilter.ALL -> true
            ProgressFilter.COMPLETE -> item.isComplete
            ProgressFilter.INCOMPLETE -> !item.isComplete
            ProgressFilter.IN_PROGRESS -> item.isInProgress
            ProgressFilter.NOT_STARTED -> item.isNotStarted
        }
    }
}

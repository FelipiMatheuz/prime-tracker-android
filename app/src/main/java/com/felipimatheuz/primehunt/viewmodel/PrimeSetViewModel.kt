package com.felipimatheuz.primehunt.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.business.resources.PrimeSetData
import com.felipimatheuz.primehunt.business.state.PrimeSetUiState
import com.felipimatheuz.primehunt.business.util.PrimeFilter
import com.felipimatheuz.primehunt.model.PrimeSet
import com.felipimatheuz.primehunt.model.PrimeStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrimeSetViewModel @Inject constructor(
    private val primeSetData: PrimeSetData,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val searchText = MutableStateFlow("")
    private val selectedPrimeSet = MutableStateFlow("")
    private val primeFilter: StateFlow<PrimeFilter> =
        savedStateHandle.getStateFlow<String?>("filter", PrimeFilter.SHOW_ALL.name)
            .map { filterName ->
                try {
                    PrimeFilter.valueOf(filterName ?: PrimeFilter.SHOW_ALL.name)
                } catch (_: IllegalArgumentException) {
                    PrimeFilter.SHOW_ALL
                }
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000L), PrimeFilter.SHOW_ALL
            )

    val uiState: StateFlow<PrimeSetUiState> =
        combine(
            primeSetData.getListSetDataFlow(),
            searchText,
            primeFilter,
            selectedPrimeSet
        ) { sets, query, currentFilterValue, selected ->
            val filteredSets = sets.filter { primeSet ->
                val filterMatches = when (currentFilterValue) {
                    PrimeFilter.SHOW_ALL -> true
                    PrimeFilter.COMPLETE -> isComplete(primeSet)
                    PrimeFilter.INCOMPLETE -> !isComplete(primeSet)
                    PrimeFilter.AVAILABLE -> primeSet.status != PrimeStatus.VAULT
                    PrimeFilter.UNAVAILABLE -> primeSet.status == PrimeStatus.VAULT
                }
                val searchMatches = if (query.isNotBlank()) {
                    primeSet.primeItems.any { primeItem ->
                        primeItem.name.contains(query, ignoreCase = true)
                    }
                } else {
                    true
                }
                filterMatches && searchMatches
            }
            PrimeSetUiState(
                primeSets = filteredSets,
                queryFilter = query,
                selectedPrimeSet = selected,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = PrimeSetUiState(emptyList(), queryFilter = "")
        )

    fun refresh() {
        viewModelScope.launch {
            selectedPrimeSet.update { "" }
        }
    }

    fun getStatusTextRes(status: PrimeStatus): Int {
        val statusText = when (status) {
            PrimeStatus.VAULT -> R.string.status_vault
            PrimeStatus.ACTIVE -> R.string.status_active
            PrimeStatus.BARO -> R.string.status_baro
            PrimeStatus.RESURGENCE -> R.string.status_resurgence
        }
        return statusText
    }

    fun togglePrimeSet(primeSet: PrimeSet, checkAll: Boolean) {
        viewModelScope.launch {
            primeSetData.togglePrimeSet(primeSet, checkAll)
        }
    }

    fun updateSearchText(text: String) {
        searchText.update { text }
    }

    fun setSelectedSet(set: String) {
        selectedPrimeSet.update { set }
    }

    private fun isComplete(primeSet: PrimeSet) = primeSet.primeItems.all { it.blueprint } &&
            primeSet.primeItems.all { primeItem ->
                primeItem.components.all { itemComponent -> itemComponent.obtained }
            }
}
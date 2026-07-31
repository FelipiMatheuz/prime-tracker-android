package com.felipimatheuz.primehunt.ui.viewmodel.primedetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felipimatheuz.primehunt.data.repository.PrimeDetailRepository
import com.felipimatheuz.primehunt.domain.usecase.primeset.GetPrimeSetsUseCase
import com.felipimatheuz.primehunt.ui.mvi.MviViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = PrimeDetailViewModel.Factory::class)
class PrimeDetailViewModel @AssistedInject constructor(
    private val repository: PrimeDetailRepository,
    getPrimeSetsUseCase: GetPrimeSetsUseCase,
    @Assisted private val setId: String
) : ViewModel(), MviViewModel<PrimeDetailState, PrimeDetailIntent> {

    @AssistedFactory
    interface Factory {
        fun create(setId: String): PrimeDetailViewModel
    }

    override val state: StateFlow<PrimeDetailState> = getPrimeSetsUseCase.observeAllSets()
        .map { allSets -> allSets.find { it.id == setId } }
        .map { PrimeDetailState(primeSet = it, isLoading = false) }
        .flowOn(Dispatchers.Default)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = PrimeDetailState()
        )

    override fun onIntent(intent: PrimeDetailIntent) {
        when (intent) {
            is PrimeDetailIntent.UpdateQuantity -> {
                viewModelScope.launch(Dispatchers.IO) {
                    repository.updateInventory(intent.partId, intent.delta)
                }
            }
            is PrimeDetailIntent.UpdateSetQuantity -> {
                viewModelScope.launch(Dispatchers.IO) {
                    repository.updateSetInventory(setId, intent.delta)
                }
            }
        }
    }
}

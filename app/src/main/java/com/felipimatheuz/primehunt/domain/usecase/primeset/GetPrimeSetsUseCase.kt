package com.felipimatheuz.primehunt.domain.usecase.primeset

import com.felipimatheuz.primehunt.domain.model.PrimeCollection
import com.felipimatheuz.primehunt.domain.model.PrimeSetDomain
import com.felipimatheuz.primehunt.domain.repository.PrimeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPrimeSetsUseCase @Inject constructor(
    private val primeRepository: PrimeRepository
) {

    fun observeAllSets(): Flow<List<PrimeSetDomain>> = primeRepository.observeAllSets()

    fun observeCollections(): Flow<List<PrimeCollection>> = primeRepository.observeCollections()

    fun observeWithoutCollection(): Flow<PrimeCollection> = primeRepository.observeWithoutCollection()
}

package com.felipimatheuz.primehunt.domain.usecase.relic

import com.felipimatheuz.primehunt.domain.model.RelicDomain
import com.felipimatheuz.primehunt.domain.repository.PrimeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRelicsUseCase @Inject constructor(
    private val primeRepository: PrimeRepository
) {

    operator fun invoke(): Flow<List<RelicDomain>> = primeRepository.observeAllRelics()
}

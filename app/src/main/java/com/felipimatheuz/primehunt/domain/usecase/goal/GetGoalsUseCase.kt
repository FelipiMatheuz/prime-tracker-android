package com.felipimatheuz.primehunt.domain.usecase.goal

import com.felipimatheuz.primehunt.domain.model.enums.GoalTargetType
import com.felipimatheuz.primehunt.data.repository.PrimeDataStore
import com.felipimatheuz.primehunt.domain.mapper.PrimeMapper
import com.felipimatheuz.primehunt.domain.model.GoalDomain
import com.felipimatheuz.primehunt.domain.model.TargetDomain
import com.felipimatheuz.primehunt.domain.repository.GoalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetGoalsUseCase @Inject constructor(
    private val primeDataStore: PrimeDataStore,
    private val goalRepository: GoalRepository
) {

    operator fun invoke(): Flow<List<GoalDomain>> = goalRepository.observeAllWithTags()

    fun observeGoal(id: Long): Flow<GoalDomain?> = goalRepository.observeByIdWithTag(id)

    fun observeAllTargets(): Flow<List<TargetDomain>> = primeDataStore.baseData.map { data ->
        val sets = data.sets
        val parts = data.parts
        val relics = data.relics
        val componentByPartMap = data.components.groupBy { it.primePartId }
        
        val blueprintIds = parts.filter { it.id == it.primeSetId }.map { it.id }.toSet()
        val setMap = sets.associateBy { it.id }

        val setTargets = sets.map {
            TargetDomain(it.id, it.name, GoalTargetType.PRIME_SET)
        }

        val synthesizedBlueprints = sets.filter { set ->
            val hasExplicitBlueprint = blueprintIds.contains(set.id)
            !hasExplicitBlueprint && componentByPartMap[set.id]?.isNotEmpty() == true
        }.map { set ->
            TargetDomain(
                set.id,
                PrimeMapper.getBlueprintName(set.name),
                GoalTargetType.PRIME_PART
            )
        }

        val partTargets = parts.map { part ->
            val setName = setMap[part.primeSetId]?.name ?: ""
            TargetDomain(part.id, PrimeMapper.formatPartName(setName, part.part), GoalTargetType.PRIME_PART)
        }

        val relicTargets = relics.map {
            TargetDomain(it.id, "${it.era.name} ${it.name}", GoalTargetType.RELIC)
        }
        
        val formaTargets = listOf(
            TargetDomain("forma_blueprint", "Forma Blueprint", GoalTargetType.FORMA)
        )

        setTargets + synthesizedBlueprints + partTargets + relicTargets + formaTargets
    }
}

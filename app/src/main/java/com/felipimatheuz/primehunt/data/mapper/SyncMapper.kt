package com.felipimatheuz.primehunt.data.mapper

import com.felipimatheuz.primehunt.data.local.entity.*
import com.felipimatheuz.primehunt.data.remote.dto.*
import com.felipimatheuz.primehunt.data.remote.enums.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncMapper @Inject constructor() {

    fun mapRelics(relics: List<RelicDto>): Pair<List<RelicEntity>, List<PrimeComponentEntity>> {
        val relicEntities = relics.map { dto ->
            RelicEntity(
                id = dto.id,
                name = dto.name,
                era = RelicEra.fromString(dto.era),
                source = RelicSource.fromString(dto.source)
            )
        }
        val componentEntities = relics.flatMap { relicDto ->
            relicDto.drops.map { dropDto ->
                PrimeComponentEntity(
                    id = "${relicDto.id}_${dropDto.id}",
                    relicId = relicDto.id,
                    primePartId = dropDto.id,
                    rarity = DropRarity.fromString(dropDto.rarity)
                )
            }
        }
        return relicEntities to componentEntities
    }

    fun mapPrimeSets(sets: List<PrimeSetDto>): Pair<List<PrimeSetEntity>, List<PrimePartEntity>> {
        val setEntities = sets.map { dto ->
            PrimeSetEntity(
                id = dto.id,
                name = dto.name,
                type = PrimeType.fromString(dto.type),
                image = dto.image
            )
        }
        val partEntities = sets.flatMap { setDto ->
            setDto.components.map { compDto ->
                PrimePartEntity(
                    id = compDto.id,
                    primeSetId = setDto.id,
                    part = PrimePartType.fromString(compDto.part),
                    quantity = compDto.quantity
                )
            }
        }
        return setEntities to partEntities
    }

    fun mapCollections(collections: List<PrimeCollectionDto>): Pair<List<PrimeCollectionEntity>, List<PrimeCollectionSetCrossRef>> {
        val collectionEntities = collections.map { dto ->
            PrimeCollectionEntity(
                id = dto.id,
                name = dto.name,
                promoImage = dto.promoImage,
                released = dto.released
            )
        }
        val crossRefs = collections.flatMap { collDto ->
            collDto.primeSets.map { setId ->
                PrimeCollectionSetCrossRef(
                    collectionId = collDto.id,
                    primeSetId = setId
                )
            }
        }
        return collectionEntities to crossRefs
    }
}

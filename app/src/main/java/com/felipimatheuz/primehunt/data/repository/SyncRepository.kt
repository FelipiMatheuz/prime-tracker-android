package com.felipimatheuz.primehunt.data.repository

import com.felipimatheuz.primehunt.data.remote.dao.*
import com.felipimatheuz.primehunt.data.remote.dto.*
import com.felipimatheuz.primehunt.data.remote.entity.*
import com.felipimatheuz.primehunt.data.remote.enums.DropRarity
import com.felipimatheuz.primehunt.data.remote.enums.PrimePartType
import com.felipimatheuz.primehunt.data.remote.enums.PrimeType
import com.felipimatheuz.primehunt.data.remote.enums.RelicEra
import com.felipimatheuz.primehunt.data.remote.enums.RelicSource
import com.felipimatheuz.primehunt.service.api.PrimeTrackerService
import com.felipimatheuz.primehunt.ui.viewmodel.splash.EtlFile
import com.felipimatheuz.primehunt.ui.viewmodel.splash.SyncEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncRepository @Inject constructor(
    private val service: PrimeTrackerService,
    private val manifestDao: ManifestDao,
    private val relicDao: RelicDao,
    private val primeSetDao: PrimeSetDao,
    private val primePartDao: PrimePartDao,
    private val primeComponentDao: PrimeComponentDao,
    private val primeCollectionDao: PrimeCollectionDao,
    private val primeCollectionSetDao: PrimeCollectionSetDao
) {

    fun performSync(): Flow<SyncEvent> = flow {
        emit(SyncEvent.Starting)
        try {
            val remoteManifest = service.readManifest()
            val localManifest = manifestDao.getManifest()

            emit(SyncEvent.CheckingManifest(remoteManifest.generatorVersion))

            var updatedFiles = 0

            // Sync Relics
            val remoteRelicsHash = remoteManifest.files.find { it.name == "relics.json" }?.sha256
            if (remoteRelicsHash != localManifest?.relicsHash) {
                emit(SyncEvent.Downloading(EtlFile.RELICS))
                val relics = service.getRelics()
                emit(SyncEvent.Importing(EtlFile.RELICS))
                importRelics(relics)
                updatedFiles++
            }

            // Sync Prime Sets
            val remoteSetsHash = remoteManifest.files.find { it.name == "prime-sets.json" }?.sha256
            if (remoteSetsHash != localManifest?.primeSetsHash) {
                emit(SyncEvent.Downloading(EtlFile.PRIME_SETS))
                val sets = service.getPrimeSets()
                emit(SyncEvent.Importing(EtlFile.PRIME_SETS))
                importPrimeSets(sets)
                updatedFiles++
            }

            // Sync Collections
            val remoteCollectionsHash =
                remoteManifest.files.find { it.name == "prime-collections.json" }?.sha256
            if (remoteCollectionsHash != localManifest?.collectionsHash) {
                emit(SyncEvent.Downloading(EtlFile.PRIME_COLLECTIONS))
                val collections = service.getPrimeCollections()
                emit(SyncEvent.Importing(EtlFile.PRIME_COLLECTIONS))
                importCollections(collections)
                updatedFiles++
            }

            if (updatedFiles > 0) {
                val newManifest = LocalManifest(
                    lastSync = System.currentTimeMillis(),
                    relicsHash = remoteRelicsHash,
                    primeSetsHash = remoteSetsHash,
                    collectionsHash = remoteCollectionsHash
                )
                manifestDao.upsert(newManifest)
                emit(SyncEvent.Success)
            } else {
                emit(SyncEvent.AlreadyUpToDate)
            }

        } catch (_: Exception) {
            emit(SyncEvent.Error)
        }
    }.flowOn(Dispatchers.IO)

    private suspend fun importRelics(relics: List<RelicDto>) {
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
        relicDao.upsertAll(relicEntities)
        primeComponentDao.upsertAll(componentEntities)
    }

    private suspend fun importPrimeSets(sets: List<PrimeSetDto>) {
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
        primeSetDao.upsertAll(setEntities)
        primePartDao.upsertAll(partEntities)
    }

    private suspend fun importCollections(collections: List<PrimeCollectionDto>) {
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
        primeCollectionDao.upsertAll(collectionEntities)
        primeCollectionSetDao.upsertAll(crossRefs)
    }
}

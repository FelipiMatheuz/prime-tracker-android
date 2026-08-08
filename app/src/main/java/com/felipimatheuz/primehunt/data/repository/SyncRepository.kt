package com.felipimatheuz.primehunt.data.repository

import com.felipimatheuz.primehunt.core.logging.AppLogger
import com.felipimatheuz.primehunt.data.local.dao.*
import com.felipimatheuz.primehunt.data.local.entity.LocalManifest
import com.felipimatheuz.primehunt.data.mapper.SyncMapper
import com.felipimatheuz.primehunt.data.remote.PrimeTrackerService
import com.felipimatheuz.primehunt.data.remote.dto.ManifestFile
import com.felipimatheuz.primehunt.domain.model.EtlFile
import com.felipimatheuz.primehunt.domain.model.SyncEvent
import com.felipimatheuz.primehunt.domain.repository.SyncRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncRepositoryImpl @Inject constructor(
    private val service: PrimeTrackerService,
    private val manifestDao: ManifestDao,
    private val relicDao: RelicDao,
    private val primeSetDao: PrimeSetDao,
    private val primePartDao: PrimePartDao,
    private val primeComponentDao: PrimeComponentDao,
    private val primeCollectionDao: PrimeCollectionDao,
    private val primeCollectionSetDao: PrimeCollectionSetDao,
    private val syncMapper: SyncMapper,
    private val logger: AppLogger
) : SyncRepository {

    override fun performSync(): Flow<SyncEvent> = flow {
        emit(SyncEvent.Starting)
        
        val localManifest = manifestDao.getManifest()
        val remoteManifestResult = runCatching { service.readManifest() }
        
        if (remoteManifestResult.isFailure) {
            logger.log("SyncRepository", "Failed to read remote manifest")
            val fallbackManifest = LocalManifest(
                lastSync = System.currentTimeMillis(),
                relicsHash = localManifest?.relicsHash,
                primeSetsHash = localManifest?.primeSetsHash,
                collectionsHash = localManifest?.collectionsHash,
                isRelicsValid = false,
                isSetsValid = false,
                isCollectionsValid = false
            )
            manifestDao.upsert(fallbackManifest)
            emit(SyncEvent.Error)
            return@flow
        }

        val remoteManifest = remoteManifestResult.getOrThrow()
        emit(SyncEvent.CheckingManifest(remoteManifest.generatorVersion))

        val remoteFiles = remoteManifest.files

        val relicsResult = performGranularSync(
            fileName = "relics.json",
            etlFile = EtlFile.RELICS,
            localHash = localManifest?.relicsHash,
            remoteFiles = remoteFiles,
            fetcher = { service.getRelics() },
            importer = { relics ->
                val (relicsEntities, components) = syncMapper.mapRelics(relics)
                relicDao.upsertAll(relicsEntities)
                primeComponentDao.upsertAll(components)
            }
        )

        val setsResult = performGranularSync(
            fileName = "prime-sets.json",
            etlFile = EtlFile.PRIME_SETS,
            localHash = localManifest?.primeSetsHash,
            remoteFiles = remoteFiles,
            fetcher = { service.getPrimeSets() },
            importer = { sets ->
                val (setsEntities, parts) = syncMapper.mapPrimeSets(sets)
                primeSetDao.upsertAll(setsEntities)
                primePartDao.upsertAll(parts)
            }
        )

        val collectionsResult = performGranularSync(
            fileName = "prime-collections.json",
            etlFile = EtlFile.PRIME_COLLECTIONS,
            localHash = localManifest?.collectionsHash,
            remoteFiles = remoteFiles,
            fetcher = { service.getPrimeCollections() },
            importer = { collections ->
                val (collectionEntities, crossRefs) = syncMapper.mapCollections(collections)
                primeCollectionDao.upsertAll(collectionEntities)
                primeCollectionSetDao.upsertAll(crossRefs)
            }
        )

        val finalManifest = LocalManifest(
            lastSync = System.currentTimeMillis(),
            relicsHash = relicsResult.hash ?: localManifest?.relicsHash,
            primeSetsHash = setsResult.hash ?: localManifest?.primeSetsHash,
            collectionsHash = collectionsResult.hash ?: localManifest?.collectionsHash,
            isRelicsValid = relicsResult.isValid,
            isSetsValid = setsResult.isValid,
            isCollectionsValid = collectionsResult.isValid
        )
        
        manifestDao.upsert(finalManifest)

        if (relicsResult.isValid && setsResult.isValid && collectionsResult.isValid) {
            val anyChanged = relicsResult.changed || setsResult.changed || collectionsResult.changed
            if (anyChanged) emit(SyncEvent.Success) else emit(SyncEvent.AlreadyUpToDate)
        } else {
            emit(SyncEvent.Error)
        }
        
    }.flowOn(Dispatchers.IO)

    private data class GranularSyncResult(val hash: String?, val changed: Boolean, val isValid: Boolean)

    private suspend fun <T> FlowCollector<SyncEvent>.performGranularSync(
        fileName: String,
        etlFile: EtlFile,
        localHash: String?,
        remoteFiles: List<ManifestFile>,
        fetcher: suspend () -> T,
        importer: suspend (T) -> Unit
    ): GranularSyncResult {
        val remoteHash = remoteFiles.find { it.name == fileName }?.sha256
        
        if (remoteHash == null) {
            return GranularSyncResult(localHash, false, false)
        }

        if (remoteHash == localHash) {
            return GranularSyncResult(localHash, false, true)
        }

        return try {
            emit(SyncEvent.Downloading(etlFile))
            val data = fetcher()
            emit(SyncEvent.Importing(etlFile))
            importer(data)
            GranularSyncResult(remoteHash, true, true)
        } catch (e: Exception) {
            logger.log("SyncRepository", "Failed to sync $fileName: ${e.message}")
            GranularSyncResult(localHash, false, false)
        }
    }
}

package com.felipimatheuz.primehunt.data.repository

import com.felipimatheuz.primehunt.core.logging.AppLogger
import com.felipimatheuz.primehunt.data.local.dao.ManifestDao
import com.felipimatheuz.primehunt.data.local.dao.PrimeCollectionDao
import com.felipimatheuz.primehunt.data.local.dao.PrimeCollectionSetDao
import com.felipimatheuz.primehunt.data.local.dao.PrimeComponentDao
import com.felipimatheuz.primehunt.data.local.dao.PrimePartDao
import com.felipimatheuz.primehunt.data.local.dao.PrimeSetDao
import com.felipimatheuz.primehunt.data.local.dao.RelicDao
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
        try {
            val remoteManifest = service.readManifest()
            val localManifest = manifestDao.getManifest()

            emit(SyncEvent.CheckingManifest(remoteManifest.generatorVersion))

            val remoteFiles = remoteManifest.files

            val newRelicsHash = syncIfNeeded(
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

            val newSetsHash = syncIfNeeded(
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

            val newCollectionsHash = syncIfNeeded(
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

            if (newRelicsHash != null || newSetsHash != null || newCollectionsHash != null) {
                val newManifest = LocalManifest(
                    lastSync = System.currentTimeMillis(),
                    relicsHash = newRelicsHash ?: localManifest?.relicsHash,
                    primeSetsHash = newSetsHash ?: localManifest?.primeSetsHash,
                    collectionsHash = newCollectionsHash ?: localManifest?.collectionsHash
                )
                manifestDao.upsert(newManifest)
                emit(SyncEvent.Success)
            } else {
                emit(SyncEvent.AlreadyUpToDate)
            }

        } catch (e: Exception) {
            logger.log("SyncRepository", "Sync failed: ${e.message}")
            emit(SyncEvent.Error)
        }
    }.flowOn(Dispatchers.IO)

    private suspend fun <T> FlowCollector<SyncEvent>.syncIfNeeded(
        fileName: String,
        etlFile: EtlFile,
        localHash: String?,
        remoteFiles: List<ManifestFile>,
        fetcher: suspend () -> T,
        importer: suspend (T) -> Unit
    ): String? {
        val remoteHash = remoteFiles.find { it.name == fileName }?.sha256
        if (remoteHash != localHash && remoteHash != null) {
            emit(SyncEvent.Downloading(etlFile))
            val data = fetcher()
            emit(SyncEvent.Importing(etlFile))
            importer(data)
            return remoteHash
        }
        return null
    }
}

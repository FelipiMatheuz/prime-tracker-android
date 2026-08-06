package com.felipimatheuz.primehunt.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.felipimatheuz.primehunt.data.local.dao.GoalDao
import com.felipimatheuz.primehunt.data.local.dao.GoalTagDao
import com.felipimatheuz.primehunt.data.local.dao.InventoryDao
import com.felipimatheuz.primehunt.data.local.entity.GoalEntity
import com.felipimatheuz.primehunt.data.local.entity.GoalTagEntity
import com.felipimatheuz.primehunt.data.local.entity.InventoryPartEntity
import com.felipimatheuz.primehunt.data.local.dao.RelicDao
import com.felipimatheuz.primehunt.data.local.dao.ManifestDao
import com.felipimatheuz.primehunt.data.local.dao.PrimeSetDao
import com.felipimatheuz.primehunt.data.local.dao.PrimePartDao
import com.felipimatheuz.primehunt.data.local.dao.PrimeComponentDao
import com.felipimatheuz.primehunt.data.local.dao.PrimeCollectionDao
import com.felipimatheuz.primehunt.data.local.dao.PrimeCollectionSetDao
import com.felipimatheuz.primehunt.data.local.entity.*

@Database(
    entities = [
        RelicEntity::class,
        LocalManifest::class,
        PrimeSetEntity::class,
        PrimePartEntity::class,
        PrimeComponentEntity::class,
        PrimeCollectionEntity::class,
        PrimeCollectionSetCrossRef::class,
        InventoryPartEntity::class,
        GoalEntity::class,
        GoalTagEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun relicDao(): RelicDao
    abstract fun manifestDao(): ManifestDao
    abstract fun primeSetDao(): PrimeSetDao
    abstract fun primePartDao(): PrimePartDao
    abstract fun primeComponentDao(): PrimeComponentDao
    abstract fun primeCollectionDao(): PrimeCollectionDao
    abstract fun primeCollectionSetDao(): PrimeCollectionSetDao
    abstract fun inventoryDao(): InventoryDao
    abstract fun goalDao(): GoalDao
    abstract fun goalTagDao(): GoalTagDao
}

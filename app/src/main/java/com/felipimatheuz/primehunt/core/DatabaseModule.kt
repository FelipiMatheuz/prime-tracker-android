package com.felipimatheuz.primehunt.core

import android.content.Context
import androidx.room.Room
import com.felipimatheuz.primehunt.data.local.dao.GoalDao
import com.felipimatheuz.primehunt.data.local.dao.GoalTagDao
import com.felipimatheuz.primehunt.data.local.dao.InventoryDao
import com.felipimatheuz.primehunt.data.local.AppDatabase
import com.felipimatheuz.primehunt.data.local.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "prime_tracker_db"
        ).build()
    }

    @Provides
    fun provideRelicDao(db: AppDatabase): RelicDao = db.relicDao()

    @Provides
    fun provideManifestDao(db: AppDatabase): ManifestDao = db.manifestDao()

    @Provides
    fun providePrimeSetDao(db: AppDatabase): PrimeSetDao = db.primeSetDao()

    @Provides
    fun providePrimePartDao(db: AppDatabase): PrimePartDao = db.primePartDao()

    @Provides
    fun providePrimeComponentDao(db: AppDatabase): PrimeComponentDao = db.primeComponentDao()

    @Provides
    fun providePrimeCollectionDao(db: AppDatabase): PrimeCollectionDao = db.primeCollectionDao()

    @Provides
    fun providePrimeCollectionSetDao(db: AppDatabase): PrimeCollectionSetDao = db.primeCollectionSetDao()

    @Provides
    fun provideInventoryDao(db: AppDatabase): InventoryDao = db.inventoryDao()

    @Provides
    fun provideGoalDao(db: AppDatabase): GoalDao = db.goalDao()

    @Provides
    fun provideGoalTagDao(db: AppDatabase): GoalTagDao = db.goalTagDao()
}

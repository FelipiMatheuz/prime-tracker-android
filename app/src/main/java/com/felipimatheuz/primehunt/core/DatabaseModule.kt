package com.felipimatheuz.primehunt.core

import android.content.Context
import androidx.room.Room
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
    @Singleton
    fun provideRelicDao(db: AppDatabase): RelicDao = db.relicDao()

    @Provides
    @Singleton
    fun provideManifestDao(db: AppDatabase): ManifestDao = db.manifestDao()

    @Provides
    @Singleton
    fun providePrimeSetDao(db: AppDatabase): PrimeSetDao = db.primeSetDao()

    @Provides
    @Singleton
    fun providePrimePartDao(db: AppDatabase): PrimePartDao = db.primePartDao()

    @Provides
    @Singleton
    fun providePrimeComponentDao(db: AppDatabase): PrimeComponentDao = db.primeComponentDao()

    @Provides
    @Singleton
    fun providePrimeCollectionDao(db: AppDatabase): PrimeCollectionDao = db.primeCollectionDao()

    @Provides
    @Singleton
    fun providePrimeCollectionSetDao(db: AppDatabase): PrimeCollectionSetDao = db.primeCollectionSetDao()

    @Provides
    @Singleton
    fun provideInventoryDao(db: AppDatabase): InventoryDao = db.inventoryDao()

    @Provides
    @Singleton
    fun provideGoalDao(db: AppDatabase): GoalDao = db.goalDao()

    @Provides
    @Singleton
    fun provideGoalTagDao(db: AppDatabase): GoalTagDao = db.goalTagDao()
}

package com.felipimatheuz.primehunt.core

import android.content.Context
import androidx.room.Room
import com.felipimatheuz.primehunt.data.remote.AppDatabase
import com.felipimatheuz.primehunt.data.remote.dao.*
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
}

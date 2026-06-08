package com.example.nutribalance.core.data.di

import android.content.Context
import androidx.room.Room
import com.example.nutribalance.core.data.local.AppDatabase
import com.example.nutribalance.core.data.local.MealDao
import com.example.nutribalance.core.data.local.MealRepositoryImpl
import com.example.nutribalance.core.data.mapper.MealMapper
import com.example.nutribalance.core.domain.repository.MealRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// Поставляет базу данных Room, DAO и репозиторий в граф зависимостей
@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "nutribalance_db"
        ).build()
    }

    @Provides
    fun provideMealDao(database: AppDatabase): MealDao = database.mealDao()

    @Provides
    @Singleton
    fun provideMealMapper(): MealMapper = MealMapper()

    @Provides
    @Singleton
    fun provideMealRepository(mealDao: MealDao, mapper: MealMapper): MealRepository {
        return MealRepositoryImpl(mealDao, mapper)
    }
}

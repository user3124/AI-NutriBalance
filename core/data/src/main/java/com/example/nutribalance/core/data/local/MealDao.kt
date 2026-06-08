package com.example.nutribalance.core.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

// Интерфейс запросов к БД (Data Access Object).
@Dao
interface MealDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(meal: MealEntity)

    @Query("DELETE FROM meals WHERE id = :mealId")
    suspend fun deleteMeal(mealId: Long)

    @Query("SELECT * FROM meals WHERE timestamp BETWEEN :startTimestamp AND :endTimestamp ORDER BY timestamp DESC")
    suspend fun getMealsByDay(startTimestamp: Long, endTimestamp: Long): List<MealEntity>

    @Query("SELECT * FROM meals WHERE type = :type AND timestamp BETWEEN :startTimestamp AND :endTimestamp ORDER BY timestamp DESC")
    suspend fun getMealsByType(type: String, startTimestamp: Long, endTimestamp: Long): List<MealEntity>
}

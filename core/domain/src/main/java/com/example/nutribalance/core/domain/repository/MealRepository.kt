package com.example.nutribalance.core.domain.repository

import com.example.nutribalance.core.domain.model.Meal
import com.example.nutribalance.core.domain.model.MealType

interface MealRepository {
    suspend fun insertMeal(meal: Meal)
    suspend fun deleteMeal(mealId: Long)
    suspend fun getMealsByDay(startTimestamp: Long, endTimestamp: Long): List<Meal>
    suspend fun getMealsByType(type: MealType, startTimestamp: Long, endTimestamp: Long): List<Meal>
}

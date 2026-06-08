package com.example.nutribalance.core.data.local

import com.example.nutribalance.core.data.mapper.MealMapper
import com.example.nutribalance.core.domain.model.Meal
import com.example.nutribalance.core.domain.model.MealType
import com.example.nutribalance.core.domain.repository.MealRepository
import javax.inject.Inject

class MealRepositoryImpl @Inject constructor(
    private val mealDao: MealDao,
    private val mapper: MealMapper
) : MealRepository {

    override suspend fun insertMeal(meal: Meal) {
        mealDao.insertMeal(mapper.mapToEntity(meal))
    }

    override suspend fun deleteMeal(mealId: Long) {
        mealDao.deleteMeal(mealId)
    }

    override suspend fun getMealsByDay(startTimestamp: Long, endTimestamp: Long): List<Meal> {
        return mealDao.getMealsByDay(startTimestamp, endTimestamp).map { mapper.mapToDomain(it) }
    }

    override suspend fun getMealsByType(type: MealType, startTimestamp: Long, endTimestamp: Long): List<Meal> {
        return mealDao.getMealsByType(type.name, startTimestamp, endTimestamp).map { mapper.mapToDomain(it) }
    }
}

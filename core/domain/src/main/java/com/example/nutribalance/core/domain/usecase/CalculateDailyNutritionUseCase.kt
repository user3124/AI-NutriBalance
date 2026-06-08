package com.example.nutribalance.core.domain.usecase

import com.example.nutribalance.core.domain.model.Meal
import javax.inject.Inject

// Изолированный сценарий использования, Use Case
// ViewModel будет вызывать его, чтобы получить сумму съеденного за день и сравнить с нормой худеющего
class CalculateDailyNutritionUseCase @Inject constructor() {

    operator fun invoke(meals: List<Meal>): DailyNutritionResult {
        var calories = 0.0
        var proteins = 0.0
        var fats = 0.0
        var carbs = 0.0

        meals.forEach { meal ->
            calories += meal.totalCalories
            proteins += meal.totalProteins
            fats += meal.totalFats
            carbs += meal.totalCarbs
        }

        return DailyNutritionResult(calories, proteins, fats, carbs)
    }
}

data class DailyNutritionResult(
    val calories: Double,
    val proteins: Double,
    val fats: Double,
    val carbs: Double
)

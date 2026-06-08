package com.example.nutribalance.core.domain.model

data class Meal(
    val id: Long = 0,
    val type: MealType,
    val timestamp: Long, // Время приема пищи
    val ingredients: List<Ingredient>
) {
    val totalCalories: Double get() = ingredients.sumOf { it.calories }
    val totalProteins: Double get() = ingredients.sumOf { it.proteins }
    val totalFats: Double get() = ingredients.sumOf { it.fats }
    val totalCarbs: Double get() = ingredients.sumOf { it.carbohydrates }
}

enum class MealType {
    BREAKFAST, LUNCH, DINNER, SNACK
}

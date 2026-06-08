package com.example.nutribalance.core.domain.model

data class Ingredient(
    val name: String,
    val weightGrams: Double,
    val calories: Double,    // Калории на порцию
    val proteins: Double,    // Белки
    val fats: Double,        // Жиры
    val carbohydrates: Double // Углеводы
)

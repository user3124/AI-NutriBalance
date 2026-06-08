package com.example.nutribalance.core.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// Объект таблицы базы данных (DTO)
// Room преобразует этот класс в SQL-таблицу
// Список ингредиентов - в виде JSON-строки
@Entity(tableName = "meals")
data class MealEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String,          // BREAKFAST, LUNCH, etc.
    val timestamp: Long,
    val ingredientsJson: String // Сериализованный список ингредиентов
)

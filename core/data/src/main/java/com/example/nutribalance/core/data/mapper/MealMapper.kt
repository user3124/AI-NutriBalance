package com.example.nutribalance.core.data.mapper

import com.example.nutribalance.core.data.local.MealEntity
import com.example.nutribalance.core.domain.model.Ingredient
import com.example.nutribalance.core.domain.model.Meal
import com.example.nutribalance.core.domain.model.MealType
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// Превращает DTO-объекты базы данных (MealEntity) в чистые бизнес-модели (Meal) из слоя Domain
class MealMapper {
    private val gson = Gson()

    fun mapToDomain(entity: MealEntity): Meal {
        val listType = object : TypeToken<List<Ingredient>>() {}.type
        val ingredients: List<Ingredient> = gson.fromJson(entity.ingredientsJson, listType) ?: emptyList()

        return Meal(
            id = entity.id,
            type = MealType.valueOf(entity.type),
            timestamp = entity.timestamp,
            ingredients = ingredients
        )
    }

    fun mapToEntity(domain: Meal): MealEntity {
        val ingredientsJson = gson.toJson(domain.ingredients)
        return MealEntity(
            id = domain.id,
            type = domain.type.name,
            timestamp = domain.timestamp,
            ingredientsJson = ingredientsJson
        )
    }
}

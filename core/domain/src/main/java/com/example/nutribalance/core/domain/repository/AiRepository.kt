package com.example.nutribalance.core.domain.repository

import com.example.nutribalance.core.domain.model.Ingredient

interface AiRepository {
    suspend fun parseMealText(text: String): List<Ingredient>
}

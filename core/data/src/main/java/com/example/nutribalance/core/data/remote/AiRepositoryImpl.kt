package com.example.nutribalance.core.data.remote

import com.example.nutribalance.core.domain.model.Ingredient
import com.example.nutribalance.core.domain.repository.AiRepository
import com.google.gson.Gson
import javax.inject.Inject

// Реализация, которая отправляет системный промпт в GigaChat,
// требуя вернуть данные строго в формате JSON,
// и маппит их в доменные ингредиенты
class AiRepositoryImpl @Inject constructor(
    private val contentApi: GigaChatContentApi
) : AiRepository {

    override suspend fun parseMealText(text: String): List<Ingredient> {
        val systemPrompt = """
            Ты — опытный диетолог. Проанализируй текст приема пищи и верни результат строго в формате JSON следующей структуры:
            {"products": [{"name": "название", "weightGrams": 100.0, "calories": 120.0, "proteins": 10.0, "fats": 5.0, "carbohydrates: 20.0"}]}
            Не пиши никакого другого текста, кроме чистого JSON. Текст:
        """.trimIndent()

        val request = ChatRequest(
            messages = listOf(
                ChatMessage(role = "system", content = systemPrompt),
                ChatMessage(role = "user", content = text)
            )
        )

        val response = contentApi.generateNutritionPlan(request)
        val jsonText = response.choices.firstOrNull()?.message?.content ?: return emptyList()

        return try {
            val parsed = Gson().fromJson(jsonText, AiNutritionResponse::class.java)
            parsed.products.map {
                Ingredient(it.name, it.weightGrams, it.calories, it.proteins, it.fats, it.carbohydrates)
            }
        } catch (e: Exception) {
            emptyList() // Возвращаем пустой список, если ИИ ошибся в формате (handled-ошибка для аналитики)
        }
    }
}

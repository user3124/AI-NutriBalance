package com.example.nutribalance.core.data.remote

// Модель ответа OAuth
data class TokenResponse(
    val access_token: String,
    val expires_at: Long
)

// Модели для запроса генерации текста
data class ChatRequest(
    val model: String = "GigaChat",
    val messages: List<ChatMessage>,
    val temperature: Float = 0.1f
)

data class ChatMessage(
    val role: String, // user или system
    val content: String
)

// Модель ответа от GigaChat
data class ChatResponse(
    val choices: List<ChatChoice>
)

data class ChatChoice(
    val message: ChatMessage
)

// Чистая модель, которую ИИ должен вернуть в формате JSON внутри текста
data class AiNutritionResponse(
    val products: List<AiProductDto>
)

data class AiProductDto(
    val name: String,
    val weightGrams: Double,
    val calories: Double,
    val proteins: Double,
    val fats: Double,
    val carbohydrates: Double
)

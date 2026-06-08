package com.example.nutribalance.core.data.remote

import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

// Интерфейсы Retrofit для работы с API Сбера
interface GigaChatAuthApi {
    @FormUrlEncoded
    @Headers("Usage-Flavour: General")
    @POST("oauth")
    suspend fun getAccessToken(
        @Header("Authorization") authHeader: String,
        @Header("RqUID") rqUid: String,
        @Field("scope") scope: String = "GIGACHAT_API_PERS"
    ): TokenResponse
}

interface GigaChatContentApi {
    @POST("v1/chat/completions")
    suspend fun generateNutritionPlan(
        @Body request: ChatRequest
    ): ChatResponse
}

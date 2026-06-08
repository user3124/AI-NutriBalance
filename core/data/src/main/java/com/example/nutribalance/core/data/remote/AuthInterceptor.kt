package com.example.nutribalance.core.data.remote

import android.content.Context
import android.util.Base64
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Interceptor
import okhttp3.Response
import java.util.UUID

class AuthInterceptor(
    private val context: Context,
    private val authApi: GigaChatAuthApi
) : Interceptor {

    private val mutex = Mutex()

    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    private val prefs = EncryptedSharedPreferences.create(
        "secret_tokens",
        masterKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Если это запрос авторизации — пропускаем его без токена Bearer
        if (originalRequest.url.encodedPath.contains("oauth")) {
            return chain.proceed(originalRequest)
        }

        val token = runBlocking {
            mutex.withLock {
                val currentToken = prefs.getString("access_token", null)
                val expiresAt = prefs.getLong("expires_at", 0L)

                // Если токен валиден (с запасом в 1 минуту), возвращаем его
                if (currentToken != null && expiresAt > (System.currentTimeMillis() + 60000)) {
                    currentToken
                } else {
                    // Иначе — принудительно обновляем через Сеть прямо на лету
                    try {
                        // Здесь должен быть твой Auth Key из кабинета Сбера (закодированный в Base64)
                        val clientSecret = "ТВОЙ_КЛИЕНТСКИЙ_СЕКРЕТ_ИЗ_LOCAL_PROPERTIES"
                        val authHeader = "Basic " + Base64.encodeToString(clientSecret.toByteArray(), Base64.NO_WRAP)
                        val rqUid = UUID.randomUUID().toString()

                        val response = authApi.getAccessToken(authHeader, rqUid)

                        prefs.edit()
                            .putString("access_token", response.access_token)
                            .putLong("expires_at", response.expires_at)
                            .apply()

                        response.access_token
                    } catch (e: Exception) {
                        null
                    }
                }
            }
        }

        if (token == null) {
            return chain.proceed(originalRequest)
        }

        // Добавляем токен в заголовок оригинального запроса
        val authenticatedRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()

        return chain.proceed(authenticatedRequest)
    }
}

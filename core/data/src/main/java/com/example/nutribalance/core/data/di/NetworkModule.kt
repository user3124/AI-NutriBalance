package com.example.nutribalance.core.data.di

import android.content.Context
import com.example.nutribalance.core.data.remote.AuthInterceptor
import com.example.nutribalance.core.data.remote.GigaChatAuthApi
import com.example.nutribalance.core.data.remote.GigaChatContentApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.InputStream
import java.security.KeyStore
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.inject.Named
import javax.inject.Singleton
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideSSLContext(@ApplicationContext context: Context): Pair<SSLContext, X509TrustManager> {
        val cf = CertificateFactory.getInstance("X.509")
        // Читаем наш добавленный сертификат Минцифры из res/raw
        val certInputStream: InputStream = context.resources.openRawResource(
            context.resources.getIdentifier("russian_trusted_root_ca", "raw", context.packageName)
        )
        val ca: X509Certificate =
            certInputStream.use { cf.generateCertificate(it) as X509Certificate }

        val keyStoreType = KeyStore.getDefaultType()
        val keyStore = KeyStore.getInstance(keyStoreType).apply {
            load(null, null)
            setCertificateEntry("ca", ca)
        }

        val tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm()
        val tmf = TrustManagerFactory.getInstance(tmfAlgorithm).apply {
            init(keyStore)
        }

        val sslContext = SSLContext.getInstance("TLS").apply {
            init(null, tmf.trustManagers, null)
        }

        val trustManager = tmf.trustManagers[0] as X509TrustManager
        return Pair(sslContext, trustManager)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        @ApplicationContext context: Context,
        // Добавил аннотацию перед аргументом sslPair,
        // чтобы запретить Kotlin генерировать скрытые ? extends знаки (чтобы Hilt мог сопоставить зависимости)
        sslPair: @JvmSuppressWildcards Pair<SSLContext, X509TrustManager>,
        @Named("AuthApi") authApi: GigaChatAuthApi
    ): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .sslSocketFactory(sslPair.first.socketFactory, sslPair.second)
            .addInterceptor(logging)
            .addInterceptor(AuthInterceptor(context, authApi))
            .build()
    }

    @Provides
    @Singleton
    @Named("AuthRetrofit")
    fun provideAuthRetrofit(
        sslPair: @JvmSuppressWildcards Pair<SSLContext, X509TrustManager>
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://sberbank.ru") // Эндпоинт авторизации Сбера
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .sslSocketFactory(sslPair.first.socketFactory, sslPair.second)
                    .build()
            )
            .build()
    }

    @Provides
    @Singleton
    @Named("AuthApi")
    fun provideGigaChatAuthApi(@Named("AuthRetrofit") retrofit: Retrofit): GigaChatAuthApi {
        return retrofit.create(GigaChatAuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideGigaChatContentApi(
        sslPair: @JvmSuppressWildcards Pair<SSLContext, X509TrustManager>,
        client: OkHttpClient
    ): GigaChatContentApi {
        return Retrofit.Builder()
            .baseUrl("https://sberbank.ru") // Эндпоинт контента ИИ
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(GigaChatContentApi::class.java)
    }
}

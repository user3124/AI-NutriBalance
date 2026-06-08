plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.example.nutribalance"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.nutribalance"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // НАСТРОЙКА FLAVORS (По твоему ТЗ)
    flavorDimensions += "version"
    productFlavors {
        create("free") {
            dimension = "version"
            applicationIdSuffix = ".free"
            buildConfigField("String", "BASE_URL", "\"https://free-nutrition.ru\"")
            buildConfigField("Boolean", "IS_AI_ENABLED", "false")
        }
        create("premium") {
            dimension = "version"
            applicationIdSuffix = ".premium"
            buildConfigField("String", "BASE_URL", "\"https://sbercloud.ru\"")
            buildConfigField("Boolean", "IS_AI_ENABLED", "true")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true // Оптимизация R8/ProGuard включена
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            applicationIdSuffix = ".debug"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    // Включаем фичи разметки
    buildFeatures {
        viewBinding = true
        compose = true
        buildConfig = true // Разрешаем генерацию BuildConfig флагов из Flavors
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
}

dependencies {
    // Наша локальная многомодульная архитектура (остается без изменений)
    implementation(project(":core:domain"))
    implementation(project(":core:data"))

    // AndroidX & UI View System
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)

    // Jetpack Compose (Используем явные стабильные строки — это решает проблему Failed to resolve)
    implementation(platform("androidx.compose:compose-bom:2024.02.02"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    debugImplementation("androidx.compose.ui:ui-tooling")

    // WorkManager & Background
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.hilt.work)
    kapt(libs.androidx.hilt.compiler)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.config)

    // Hilt DI
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
}

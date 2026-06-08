package com.example.nutribalance.di

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

// Для Hilt жизненно необходим кастомный класс Application
@HiltAndroidApp
class AppApplication : Application()

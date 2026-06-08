package com.example.nutribalance.background

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.nutribalance.core.domain.repository.MealRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.Calendar

@HiltWorker
class NutritionReminderWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val mealRepository: MealRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }
        val startOfDay = calendar.timeInMillis
        val endOfDay = startOfDay + 86400000L

        return try {
            val mealsToday = mealRepository.getMealsByDay(startOfDay, endOfDay)
            if (mealsToday.isEmpty()) {
                sendNotification()
            }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private fun sendNotification() {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "reminder_channel"

        // ИСПРАВЛЕНО: заменили запятую на точку в VERSION_CODES.O
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Напоминания", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Пора худеть!")
            .setContentText("Вы забыли заполнить дневник калорий за сегодня. Внесите еду через AI-ассистента.")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(2002, notification)
    }
}

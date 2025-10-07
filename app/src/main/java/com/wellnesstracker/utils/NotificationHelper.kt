package com.wellnesstracker.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.wellnesstracker.MainActivity
import com.wellnesstracker.R
import java.util.concurrent.TimeUnit

class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "wellness_channel"
        const val CHANNEL_NAME = "Wellness Reminders"
        const val NOTIFICATION_ID = 1001
        const val WORK_NAME = "water_reminder"
        const val ONE_TIME_WORK_NAME = "water_reminder_one_time"
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Reminders for hydration and wellness"
            }

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun scheduleWaterReminder(intervalMinutes: Int) {
        val safeInterval = intervalMinutes.coerceAtLeast(15)

        val workRequest = PeriodicWorkRequestBuilder<WaterReminderWorker>(
            safeInterval.toLong(), TimeUnit.MINUTES,
            15, TimeUnit.MINUTES // Flex interval
        ).setInitialDelay(safeInterval.toLong(), TimeUnit.MINUTES)
            .setConstraints(
                Constraints.Builder()
                    .setRequiresBatteryNotLow(false)
                    .build()
            )
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            workRequest
        )
    }

    fun scheduleOneTimeWaterReminder(delaySeconds: Long) {
        val safeDelay = delaySeconds.coerceAtLeast(10L)

        val workRequest = OneTimeWorkRequestBuilder<WaterReminderWorker>()
            .setInitialDelay(safeDelay, TimeUnit.SECONDS)
            .setInputData(
                workDataOf(WaterReminderWorker.KEY_IS_ONE_TIME to true)
            )
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            ONE_TIME_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            workRequest
        )

        val timestamp = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(safeDelay)
        DataManager(context).setNextHydrationReminderTime(timestamp)
    }

    fun cancelOneTimeWaterReminder() {
        WorkManager.getInstance(context).cancelUniqueWork(ONE_TIME_WORK_NAME)
        DataManager(context).clearNextHydrationReminderTime()
    }

    fun cancelWaterReminder() {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        cancelOneTimeWaterReminder()
    }

    fun showWaterReminder() {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_water)
            .setContentTitle("💧 Time to Hydrate!")
            .setContentText("Don't forget to drink water")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}


class WaterReminderWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    companion object {
        const val KEY_IS_ONE_TIME = "is_one_time"
    }

    override fun doWork(): Result {
        val dataManager = DataManager(applicationContext)
        val isOneTime = inputData.getBoolean(KEY_IS_ONE_TIME, false)

        if (dataManager.areNotificationsEnabled() || isOneTime) {
            NotificationHelper(applicationContext).showWaterReminder()
        }

        if (isOneTime) {
            dataManager.clearNextHydrationReminderTime()
        }
        return Result.success()
    }
}
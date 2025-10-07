package com.wellnesstracker.utils

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.wellnesstracker.MainActivity
import com.wellnesstracker.R
import com.wellnesstracker.receivers.HydrationReminderReceiver
import java.util.concurrent.TimeUnit

class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "wellness_channel"
        const val CHANNEL_NAME = "Wellness Reminders"
        const val NOTIFICATION_ID = 1001
        const val WORK_NAME = "water_reminder"
        private const val REQUEST_CODE_ONE_TIME_REMINDER = 201
        internal const val ACTION_ONE_TIME_REMINDER =
            "com.wellnesstracker.action.ONE_TIME_WATER_REMINDER"
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
        val triggerAt = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(safeDelay)

        cancelOneTimeWaterReminder()

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = buildOneTimeReminderPendingIntent(PendingIntent.FLAG_UPDATE_CURRENT)

        if (pendingIntent != null) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerAt,
                        pendingIntent
                    )
                } else {
                    @Suppress("DEPRECATION")
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
                }
            } catch (securityException: SecurityException) {
                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
            }
            DataManager(context).setNextHydrationReminderTime(triggerAt)
        }
    }

    fun cancelOneTimeWaterReminder() {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = buildOneTimeReminderPendingIntent(PendingIntent.FLAG_NO_CREATE)

        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }

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

    private fun buildOneTimeReminderPendingIntent(flag: Int): PendingIntent? {
        val immutableFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE
        } else {
            0
        }

        val intent = Intent(context, HydrationReminderReceiver::class.java).apply {
            action = ACTION_ONE_TIME_REMINDER
        }

        return PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_ONE_TIME_REMINDER,
            intent,
            flag or immutableFlag
        )
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

        if (dataManager.areNotificationsEnabled()) {
            NotificationHelper(applicationContext).showWaterReminder()
        }

        return Result.success()
    }
}

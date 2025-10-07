package com.wellnesstracker.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.wellnesstracker.utils.DataManager
import com.wellnesstracker.utils.NotificationHelper

class HydrationReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != NotificationHelper.ACTION_ONE_TIME_REMINDER) {
            return
        }

        NotificationHelper(context).showWaterReminder()
        DataManager(context).clearNextHydrationReminderTime()
    }
}

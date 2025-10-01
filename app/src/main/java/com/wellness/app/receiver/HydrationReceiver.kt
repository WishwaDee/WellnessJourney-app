package com.wellness.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.wellness.app.utils.NotificationHelper

class HydrationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationHelper = NotificationHelper(context)
        notificationHelper.showHydrationNotification()
    }
}

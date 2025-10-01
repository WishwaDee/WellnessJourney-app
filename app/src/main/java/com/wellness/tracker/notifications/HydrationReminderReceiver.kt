package com.wellness.tracker.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.wellness.tracker.data.PreferencesManager

class HydrationReminderReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        val preferencesManager = PreferencesManager(context)
        
        if (preferencesManager.isHydrationReminderEnabled()) {
            HydrationReminderService.showNotification(context)
        }
    }
}
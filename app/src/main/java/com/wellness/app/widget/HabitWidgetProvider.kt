package com.wellness.app.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.wellness.app.MainActivity
import com.wellness.app.R
import com.wellness.app.utils.DataManager

class HabitWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val dataManager = DataManager(context)
        val completionPercentage = dataManager.getTodayCompletionPercentage()

        val views = RemoteViews(context.packageName, R.layout.widget_habit)
        views.setTextViewText(R.id.widgetPercentage, "${completionPercentage.toInt()}%")
        views.setProgressBar(R.id.widgetProgressBar, 100, completionPercentage.toInt(), false)

        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widgetLayout, pendingIntent)

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}

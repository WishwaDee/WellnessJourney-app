package com.wellness.app.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wellness.app.models.Habit
import com.wellness.app.models.MoodEntry

class DataManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("wellness_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveHabits(habits: List<Habit>) {
        val json = gson.toJson(habits)
        prefs.edit().putString("habits", json).apply()
    }

    fun getHabits(): MutableList<Habit> {
        val json = prefs.getString("habits", null) ?: return mutableListOf()
        val type = object : TypeToken<MutableList<Habit>>() {}.type
        return gson.fromJson(json, type)
    }

    fun saveMoodEntries(entries: List<MoodEntry>) {
        val json = gson.toJson(entries)
        prefs.edit().putString("mood_entries", json).apply()
    }

    fun getMoodEntries(): MutableList<MoodEntry> {
        val json = prefs.getString("mood_entries", null) ?: return mutableListOf()
        val type = object : TypeToken<MutableList<MoodEntry>>() {}.type
        return gson.fromJson(json, type)
    }

    fun setHydrationInterval(minutes: Int) {
        prefs.edit().putInt("hydration_interval", minutes).apply()
    }

    fun getHydrationInterval(): Int {
        return prefs.getInt("hydration_interval", 60)
    }

    fun setHydrationEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("hydration_enabled", enabled).apply()
    }

    fun isHydrationEnabled(): Boolean {
        return prefs.getBoolean("hydration_enabled", false)
    }

    fun setDailyGoal(goal: Int) {
        prefs.edit().putInt("daily_goal", goal).apply()
    }

    fun getDailyGoal(): Int {
        return prefs.getInt("daily_goal", 3)
    }

    fun getTodayCompletionPercentage(): Float {
        val habits = getHabits()
        if (habits.isEmpty()) return 0f
        val completedCount = habits.count { it.isCompletedToday() }
        return (completedCount.toFloat() / habits.size.toFloat()) * 100f
    }
}

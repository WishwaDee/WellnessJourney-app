package com.wellness.tracker.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wellness.tracker.models.Habit
import com.wellness.tracker.models.HabitProgress
import com.wellness.tracker.models.MoodEntry

class PreferencesManager(context: Context) {
    
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences("wellness_tracker_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    // Habits
    fun saveHabits(habits: List<Habit>) {
        val json = gson.toJson(habits)
        sharedPreferences.edit().putString("habits", json).apply()
    }

    fun getHabits(): List<Habit> {
        val json = sharedPreferences.getString("habits", null)
        return if (json != null) {
            val type = object : TypeToken<List<Habit>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }

    // Habit Progress
    fun saveHabitProgress(progressList: List<HabitProgress>) {
        val json = gson.toJson(progressList)
        sharedPreferences.edit().putString("habit_progress", json).apply()
    }

    fun getHabitProgress(): List<HabitProgress> {
        val json = sharedPreferences.getString("habit_progress", null)
        return if (json != null) {
            val type = object : TypeToken<List<HabitProgress>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }

    // Mood Entries
    fun saveMoodEntries(moodEntries: List<MoodEntry>) {
        val json = gson.toJson(moodEntries)
        sharedPreferences.edit().putString("mood_entries", json).apply()
    }

    fun getMoodEntries(): List<MoodEntry> {
        val json = sharedPreferences.getString("mood_entries", null)
        return if (json != null) {
            val type = object : TypeToken<List<MoodEntry>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }

    // Hydration Settings
    fun setHydrationReminderEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean("hydration_reminder_enabled", enabled).apply()
    }

    fun isHydrationReminderEnabled(): Boolean {
        return sharedPreferences.getBoolean("hydration_reminder_enabled", false)
    }

    fun setHydrationInterval(intervalMinutes: Int) {
        sharedPreferences.edit().putInt("hydration_interval", intervalMinutes).apply()
    }

    fun getHydrationInterval(): Int {
        return sharedPreferences.getInt("hydration_interval", 60) // Default 1 hour
    }

    // User Settings
    fun setUserName(name: String) {
        sharedPreferences.edit().putString("user_name", name).apply()
    }

    fun getUserName(): String {
        return sharedPreferences.getString("user_name", "User") ?: "User"
    }
}
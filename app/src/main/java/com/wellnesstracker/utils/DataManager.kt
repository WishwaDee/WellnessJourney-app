package com.wellnesstracker.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wellnesstracker.models.Habit
import com.wellnesstracker.models.HabitCompletion
import com.wellnesstracker.models.MoodEntry
import java.text.SimpleDateFormat
import java.util.*

class DataManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("WellnessTrackerPrefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val KEY_HABITS = "habits"
        private const val KEY_COMPLETIONS = "completions"
        private const val KEY_MOODS = "moods"
        private const val KEY_WATER_INTERVAL = "water_interval"
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
    }

    // Habits
    fun saveHabits(habits: List<Habit>) {
        prefs.edit().putString(KEY_HABITS, gson.toJson(habits)).apply()
    }

    fun getHabits(): MutableList<Habit> {
        val json = prefs.getString(KEY_HABITS, null) ?: return mutableListOf()
        val type = object : TypeToken<MutableList<Habit>>() {}.type
        return gson.fromJson(json, type) ?: mutableListOf()
    }

    fun addHabit(habit: Habit) {
        val habits = getHabits()
        habits.add(habit)
        saveHabits(habits)
    }

    fun deleteHabit(habitId: String) {
        val habits = getHabits().filter { it.id != habitId }
        saveHabits(habits)

        // Also delete completions for this habit
        val completions = getCompletions().filter { it.habitId != habitId }
        saveCompletions(completions)
    }

    fun updateHabit(habit: Habit) {
        val habits = getHabits()
        val index = habits.indexOfFirst { it.id == habit.id }
        if (index != -1) {
            habits[index] = habit
            saveHabits(habits)
        }
    }

    // Habit Completions
    fun saveCompletions(completions: List<HabitCompletion>) {
        prefs.edit().putString(KEY_COMPLETIONS, gson.toJson(completions)).apply()
    }

    fun getCompletions(): MutableList<HabitCompletion> {
        val json = prefs.getString(KEY_COMPLETIONS, null) ?: return mutableListOf()
        val type = object : TypeToken<MutableList<HabitCompletion>>() {}.type
        return gson.fromJson(json, type) ?: mutableListOf()
    }

    fun toggleHabitCompletion(habitId: String, date: String) {
        val completions = getCompletions()
        val existing = completions.find { it.habitId == habitId && it.date == date }

        if (existing != null) {
            completions.remove(existing)
            completions.add(existing.copy(completed = !existing.completed))
        } else {
            completions.add(HabitCompletion(habitId, date, true))
        }
        saveCompletions(completions)
    }

    fun isHabitCompleted(habitId: String, date: String): Boolean {
        return getCompletions().any {
            it.habitId == habitId && it.date == date && it.completed
        }
    }

    fun getTodayCompletionPercentage(): Int {
        val today = getTodayDate()
        val habits = getHabits()
        if (habits.isEmpty()) return 0

        val completedCount = habits.count { isHabitCompleted(it.id, today) }
        return (completedCount * 100) / habits.size
    }

    // Mood Entries
    fun saveMoods(moods: List<MoodEntry>) {
        prefs.edit().putString(KEY_MOODS, gson.toJson(moods)).apply()
    }

    fun getMoods(): MutableList<MoodEntry> {
        val json = prefs.getString(KEY_MOODS, null) ?: return mutableListOf()
        val type = object : TypeToken<MutableList<MoodEntry>>() {}.type
        return gson.fromJson(json, type) ?: mutableListOf()
    }

    fun addMood(mood: MoodEntry) {
        val moods = getMoods()
        moods.add(0, mood) // Add at beginning for newest first
        saveMoods(moods)
    }

    fun deleteMood(moodId: String) {
        val moods = getMoods().filter { it.id != moodId }
        saveMoods(moods)
    }

    // Settings
    fun setWaterInterval(minutes: Int) {
        prefs.edit().putInt(KEY_WATER_INTERVAL, minutes).apply()
    }

    fun getWaterInterval(): Int {
        return prefs.getInt(KEY_WATER_INTERVAL, 60) // Default 60 minutes
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled).apply()
    }

    fun areNotificationsEnabled(): Boolean {
        return prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, true)
    }

    // Utility
    fun getTodayDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }
}
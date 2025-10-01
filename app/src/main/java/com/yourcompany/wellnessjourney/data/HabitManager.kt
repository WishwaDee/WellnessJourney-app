package com.yourcompany.wellnessjourney.data // Make sure this matches your package structure

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HabitManager(private val context: Context) {

    private val PREFS_NAME = "wellness_journey_prefs"
    private val HABITS_KEY = "all_habits" // Key for all habits
    private val DAILY_HABIT_STATE_PREFIX = "daily_habit_state_" // Prefix for daily completion state
    private val gson = Gson()

    private val sharedPrefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // Save a new habit or update an existing one
    fun addHabit(habit: Habit) {
        val allHabits = getAllHabits().toMutableList()
        allHabits.add(habit)
        saveAllHabits(allHabits)
    }

    fun updateHabit(updatedHabit: Habit) {
        val allHabits = getAllHabits().toMutableList()
        val index = allHabits.indexOfFirst { it.id == updatedHabit.id }
        if (index != -1) {
            allHabits[index] = updatedHabit
            saveAllHabits(allHabits)
        }
    }

    fun deleteHabit(habit: Habit) {
        val allHabits = getAllHabits().toMutableList()
        allHabits.removeIf { it.id == habit.id }
        saveAllHabits(allHabits)
    }

    // Get all habits stored in SharedPreferences
    fun getAllHabits(): List<Habit> {
        val json = sharedPrefs.getString(HABITS_KEY, null)
        return if (json != null) {
            gson.fromJson(json, object : TypeToken<List<Habit>>() {}.type)
        } else {
            emptyList()
        }
    }

    // Save the list of all habits (serialization)
    private fun saveAllHabits(habits: List<Habit>) {
        val json = gson.toJson(habits)
        sharedPrefs.edit().putString(HABITS_KEY, json).apply()
    }

    // --- Daily Habit State Management (for completion and value) ---

    // Get habits for the current day with their completion status
    fun getHabitsForToday(): List<Habit> {
        val allHabits = getAllHabits()
        val todayKey = getTodayFormattedDate()

        return allHabits.map { habit ->
            val dailyStateJson = sharedPrefs.getString("$DAILY_HABIT_STATE_PREFIX${habit.id}_$todayKey", null)
            if (dailyStateJson != null) {
                gson.fromJson(dailyStateJson, Habit::class.java) // Deserialize a single habit state
            } else {
                // If no state saved for today, return original habit (uncompleted for today)
                habit.copy(isCompleted = false, completionValue = 0)
            }
        }
    }

    // Update a habit's completion state for the current day
    fun updateHabitCompletion(habit: Habit, isCompleted: Boolean, completionValue: Int) {
        habit.isCompleted = isCompleted
        habit.completionValue = completionValue

        val todayKey = getTodayFormattedDate()
        val json = gson.toJson(habit) // Serialize the current state of the habit
        sharedPrefs.edit().putString("$DAILY_HABIT_STATE_PREFIX${habit.id}_$todayKey", json).apply()
    }

    // Helper to get today's date in a consistent format
    private fun getTodayFormattedDate(): String {
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        return dateFormat.format(Date())
    }

    // Function to reset all daily habit states (for debugging/end-of-day)
    fun resetDailyHabitStates() {
        val editor = sharedPrefs.edit()
        val allKeys = sharedPrefs.all.keys
        for (key in allKeys) {
            if (key.startsWith(DAILY_HABIT_STATE_PREFIX)) {
                editor.remove(key)
            }
        }
        editor.apply()
    }

    // Function to clear all saved habits (for development/reset)
    fun clearAllHabitsData() {
        sharedPrefs.edit().remove(HABITS_KEY).apply()
        resetDailyHabitStates() // Also clear daily states
    }
}
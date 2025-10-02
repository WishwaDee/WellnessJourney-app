package com.wellness.app.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wellness.app.models.Habit
import com.wellness.app.models.MoodEntry
import java.util.Calendar
import java.util.Locale

class DataManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("wellness_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveHabits(habits: List<Habit>) {
        val json = gson.toJson(habits)
        prefs.edit().putString("habits", json).apply()
    }

    fun getHabits(): MutableList<Habit> {
        val json = prefs.getString("habits", null)
        val habits: MutableList<Habit> = if (json.isNullOrEmpty()) {
            createDefaultHabits()
        } else {
            val type = object : TypeToken<MutableList<Habit>>() {}.type
            gson.fromJson(json, type)
        }

        if (habits.isEmpty()) {
            habits.addAll(createDefaultHabits())
        }

        return habits
    }

    fun updateHabit(habit: Habit) {
        val habits = getHabits()
        val index = habits.indexOfFirst { it.id == habit.id }
        if (index >= 0) {
            habits[index] = habit
        } else {
            habits.add(habit)
        }
        saveHabits(habits)
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

    fun setHydrationGoal(goal: Int) {
        prefs.edit().putInt("hydration_goal", goal).apply()
    }

    fun getHydrationGoal(): Int {
        return prefs.getInt("hydration_goal", 2000)
    }

    fun addHydration(amount: Int) {
        val history = getHydrationHistory()
        val today = getTodayKey()
        val updated = (history[today] ?: 0) + amount
        history[today] = updated
        saveHydrationHistory(history)
    }

    fun setHydrationForToday(amount: Int) {
        val history = getHydrationHistory()
        history[getTodayKey()] = amount
        saveHydrationHistory(history)
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

    fun getTodayHydration(): Int {
        val history = getHydrationHistory()
        return history[getTodayKey()] ?: 0
    }

    fun getHydrationHistory(): MutableMap<String, Int> {
        val json = prefs.getString("hydration_history", null) ?: return mutableMapOf()
        val type = object : TypeToken<MutableMap<String, Int>>() {}.type
        return gson.fromJson(json, type)
    }

    fun getHydrationCompletionPercentage(): Int {
        val goal = getHydrationGoal()
        if (goal <= 0) return 0
        return ((getTodayHydration().coerceAtMost(goal).toFloat() / goal.toFloat()) * 100f).toInt()
    }

    fun getHydrationRemaining(): Int {
        return (getHydrationGoal() - getTodayHydration()).coerceAtLeast(0)
    }

    private fun saveHydrationHistory(history: MutableMap<String, Int>) {
        val json = gson.toJson(history)
        prefs.edit().putString("hydration_history", json).apply()
    }

    fun getHabitSummary(): HabitSummary {
        val habits = getHabits()
        if (habits.isEmpty()) {
            return HabitSummary(0, 0, 0f)
        }

        val completedCount = habits.count { it.isCompletedToday() }
        val averageProgress = habits.sumOf { habit ->
            habit.getTodayProgress().coerceAtMost(habit.goal).toFloat() / habit.goal.toFloat()
        } / habits.size

        return HabitSummary(
            completedCount = completedCount,
            totalHabits = habits.size,
            completionPercentage = (averageProgress * 100f)
        )
    }

    fun getTodayCompletionPercentage(): Float {
        val summary = getHabitSummary()
        return summary.completionPercentage.coerceIn(0f, 100f)
    }

    fun getHabitStreak(): Int {
        val habits = getHabits()
        if (habits.isEmpty()) return 0

        var streak = 0
        val calendar = Calendar.getInstance()

        while (true) {
            val dateKey = String.format(
                Locale.getDefault(),
                "%04d-%02d-%02d",
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH)
            )

            val allComplete = habits.all { habit ->
                val progress = habit.progressByDate[dateKey] ?: 0
                progress >= habit.goal
            }

            if (allComplete) {
                streak++
                calendar.add(Calendar.DAY_OF_MONTH, -1)
            } else {
                break
            }
        }

        return streak
    }

    fun resetAllData() {
        val onboardingComplete = prefs.getBoolean("onboarding_complete", false)
        prefs.edit().clear().apply()
        if (onboardingComplete) {
            prefs.edit().putBoolean("onboarding_complete", true).apply()
        }
        createDefaultHabits()
    }

    private fun createDefaultHabits(): MutableList<Habit> {
        val defaults = mutableListOf(
            Habit(
                id = "habit_exercise",
                name = "Exercise",
                description = "Stay active with movement",
                goal = 30,
                unit = "minutes",
                step = 5,
                icon = "🏃",
                color = "#FFE3E3"
            ),
            Habit(
                id = "habit_meditation",
                name = "Meditation",
                description = "Take time to breathe",
                goal = 10,
                unit = "minutes",
                step = 5,
                icon = "🧘",
                color = "#E3F2FD"
            ),
            Habit(
                id = "habit_reading",
                name = "Reading",
                description = "Learn and unwind",
                goal = 20,
                unit = "minutes",
                step = 5,
                icon = "📚",
                color = "#F3E5F5"
            ),
            Habit(
                id = "habit_sleep",
                name = "Sleep",
                description = "Rest and recharge",
                goal = 8,
                unit = "hours",
                step = 1,
                icon = "😴",
                color = "#E8F5E9"
            )
        )
        saveHabits(defaults)
        return defaults
    }

    private fun getTodayKey(): String {
        val calendar = Calendar.getInstance()
        return String.format(
            Locale.getDefault(),
            "%04d-%02d-%02d",
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }
}

data class HabitSummary(
    val completedCount: Int,
    val totalHabits: Int,
    val completionPercentage: Float
)

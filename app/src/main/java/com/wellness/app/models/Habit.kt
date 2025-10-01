package com.wellness.app.models

import java.io.Serializable
import java.util.Locale

data class Habit(
    val id: String,
    var name: String,
    var description: String,
    var goal: Int,
    var unit: String,
    var step: Int,
    var icon: String,
    var color: String,
    var progressByDate: MutableMap<String, Int> = mutableMapOf(),
    val createdDate: Long = System.currentTimeMillis()
) : Serializable {

    fun getTodayProgress(): Int {
        return progressByDate[getTodayDateString()] ?: 0
    }

    fun setTodayProgress(value: Int) {
        val clamped = value.coerceIn(0, goal)
        progressByDate[getTodayDateString()] = clamped
    }

    fun adjustTodayProgress(delta: Int) {
        val current = getTodayProgress()
        setTodayProgress(current + delta)
    }

    fun getCompletionPercentage(): Int {
        if (goal <= 0) return 0
        val progress = getTodayProgress().coerceAtMost(goal)
        return ((progress.toFloat() / goal.toFloat()) * 100f).toInt()
    }

    fun isCompletedToday(): Boolean {
        return getTodayProgress() >= goal
    }

    fun getLast7DayCompletionAverage(): Int {
        if (goal <= 0) return 0
        val dates = getLast7Days()
        if (dates.isEmpty()) return 0
        val progressSum = dates.sumOf { date ->
            val progress = progressByDate[date] ?: 0
            progress.coerceAtMost(goal).toFloat() / goal.toFloat()
        }
        return ((progressSum / dates.size) * 100f).toInt()
    }

    private fun getTodayDateString(): String {
        val calendar = java.util.Calendar.getInstance()
        return String.format(
            Locale.getDefault(),
            "%04d-%02d-%02d",
            calendar.get(java.util.Calendar.YEAR),
            calendar.get(java.util.Calendar.MONTH) + 1,
            calendar.get(java.util.Calendar.DAY_OF_MONTH)
        )
    }

    private fun getLast7Days(): List<String> {
        val dates = mutableListOf<String>()
        val calendar = java.util.Calendar.getInstance()
        for (i in 0 until 7) {
            dates.add(
                String.format(
                    Locale.getDefault(),
                    "%04d-%02d-%02d",
                    calendar.get(java.util.Calendar.YEAR),
                    calendar.get(java.util.Calendar.MONTH) + 1,
                    calendar.get(java.util.Calendar.DAY_OF_MONTH)
                )
            )
            calendar.add(java.util.Calendar.DAY_OF_MONTH, -1)
        }
        return dates
    }
}

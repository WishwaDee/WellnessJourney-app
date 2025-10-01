package com.wellness.app.models

import java.io.Serializable

data class Habit(
    val id: String,
    var name: String,
    var description: String,
    var completedDates: MutableSet<String> = mutableSetOf(),
    val createdDate: Long = System.currentTimeMillis()
) : Serializable {

    fun isCompletedToday(): Boolean {
        val today = getTodayDateString()
        return completedDates.contains(today)
    }

    fun toggleCompletion() {
        val today = getTodayDateString()
        if (completedDates.contains(today)) {
            completedDates.remove(today)
        } else {
            completedDates.add(today)
        }
    }

    fun getCompletionPercentageThisWeek(): Float {
        val datesThisWeek = getLast7Days()
        val completedCount = datesThisWeek.count { completedDates.contains(it) }
        return (completedCount.toFloat() / 7f) * 100f
    }

    private fun getTodayDateString(): String {
        val calendar = java.util.Calendar.getInstance()
        return "${calendar.get(java.util.Calendar.YEAR)}-" +
                "${calendar.get(java.util.Calendar.MONTH) + 1}-" +
                "${calendar.get(java.util.Calendar.DAY_OF_MONTH)}"
    }

    private fun getLast7Days(): List<String> {
        val dates = mutableListOf<String>()
        val calendar = java.util.Calendar.getInstance()
        for (i in 0..6) {
            dates.add("${calendar.get(java.util.Calendar.YEAR)}-" +
                    "${calendar.get(java.util.Calendar.MONTH) + 1}-" +
                    "${calendar.get(java.util.Calendar.DAY_OF_MONTH)}")
            calendar.add(java.util.Calendar.DAY_OF_MONTH, -1)
        }
        return dates
    }
}

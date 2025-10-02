package com.wellnesstracker.models

/**
 * Stores the completion state of a habit for a particular day.
 */
data class HabitCompletion(
    val habitId: String,
    val date: String,
    val completed: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

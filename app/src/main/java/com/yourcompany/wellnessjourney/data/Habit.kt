package com.yourcompany.wellnessjourney.data // Make sure this matches your package structure

import java.util.UUID

// Data class to represent a single daily habit
data class Habit(
    val id: String = UUID.randomUUID().toString(), // Unique ID for each habit
    var name: String,
    var goal: String, // e.g., "30 minutes", "8 glasses", "10000 steps"
    var iconResId: Int = 0, // Resource ID for an icon (e.g., R.drawable.ic_exercise)
    var isCompleted: Boolean = false, // True if the habit is completed for the current day
    var completionValue: Int = 0 // e.g., minutes exercised, ml of water drunk, steps walked
)
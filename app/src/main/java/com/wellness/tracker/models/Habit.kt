package com.wellness.tracker.models

import java.text.SimpleDateFormat
import java.util.*

data class Habit(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String = "",
    val targetValue: Int = 1,
    val unit: String = "times",
    val createdDate: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
)

data class HabitProgress(
    val habitId: String,
    val date: String,
    val currentValue: Int = 0,
    val isCompleted: Boolean = false
)
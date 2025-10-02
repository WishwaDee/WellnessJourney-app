package com.wellnesstracker.models

import java.io.Serializable
import java.util.UUID

data class Habit(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String = "",
    val icon: String = "💧",
    val createdAt: Long = System.currentTimeMillis()
) : Serializable

data class HabitCompletion(
    val habitId: String,
    val date: String, // Format: yyyy-MM-dd
    val completed: Boolean,
    val timestamp: Long = System.currentTimeMillis()
) : Serializable
package com.wellnesstracker.models

import java.util.UUID

/**
 * Represents a single habit that the user can track.
 */
data class Habit(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String,
    val icon: String,
    val createdAt: Long = System.currentTimeMillis()
)

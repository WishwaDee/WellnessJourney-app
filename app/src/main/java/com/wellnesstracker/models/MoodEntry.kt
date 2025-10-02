package com.wellnesstracker.models

import java.util.UUID

/**
 * Represents a single mood journal entry logged by the user.
 */
data class MoodEntry(
    val id: String = UUID.randomUUID().toString(),
    val emoji: String,
    val moodName: String,
    val note: String? = null,
    val date: String,
    val timestamp: Long = System.currentTimeMillis()
)

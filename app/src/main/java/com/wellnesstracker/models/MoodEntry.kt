package com.wellnesstracker.models

import java.io.Serializable
import java.util.UUID

data class MoodEntry(
    val id: String = UUID.randomUUID().toString(),
    val emoji: String,
    val moodName: String,
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val date: String // Format: yyyy-MM-dd
) : Serializable
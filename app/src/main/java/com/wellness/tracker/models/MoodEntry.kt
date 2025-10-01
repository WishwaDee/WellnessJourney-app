package com.wellness.tracker.models

import java.text.SimpleDateFormat
import java.util.*

data class MoodEntry(
    val id: String = UUID.randomUUID().toString(),
    val emoji: String,
    val mood: String,
    val note: String = "",
    val date: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
    val time: String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
    val timestamp: Long = System.currentTimeMillis()
)
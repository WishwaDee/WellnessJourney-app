package com.wellness.app.models

import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

data class MoodEntry(
    val id: String,
    val emoji: String,
    val moodName: String,
    val note: String,
    val timestamp: Long = System.currentTimeMillis()
) : Serializable {

    fun getFormattedDate(): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    fun getFormattedTime(): String {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    fun getDateKey(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}

object MoodEmojis {
    val moods = listOf(
        Pair("😄", "Very Happy"),
        Pair("😊", "Happy"),
        Pair("😐", "Neutral"),
        Pair("😔", "Sad"),
        Pair("😢", "Very Sad"),
        Pair("😡", "Angry"),
        Pair("😰", "Anxious"),
        Pair("😴", "Tired"),
        Pair("🤗", "Grateful"),
        Pair("💪", "Motivated")
    )
}

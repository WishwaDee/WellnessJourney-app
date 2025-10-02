package com.wellnesstracker.models

import java.io.Serializable
import java.util.UUID

/**
 * Represents a single hydration event recorded by the user.
 */
data class HydrationEntry(
    val id: String = UUID.randomUUID().toString(),
    val amountMl: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val date: String
) : Serializable
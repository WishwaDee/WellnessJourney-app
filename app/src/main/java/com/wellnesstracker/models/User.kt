package com.wellnesstracker.models

import java.io.Serializable
import java.util.UUID

data class User(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val email: String,
    val password: String
) : Serializable
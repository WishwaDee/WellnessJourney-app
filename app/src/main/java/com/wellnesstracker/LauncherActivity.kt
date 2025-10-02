package com.wellnesstracker

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.wellnesstracker.utils.SessionManager

/**
 * Entry point that decides whether to show onboarding, authentication, or the main dashboard.
 */
class LauncherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sessionManager = SessionManager(this)

        when {
            !sessionManager.isOnboardingCompleted() -> {
                startActivity(Intent(this, OnboardingActivity::class.java))
            }
            !sessionManager.isLoggedIn() -> {
                startActivity(Intent(this, LoginActivity::class.java))
            }
            else -> {
                startActivity(Intent(this, MainActivity::class.java))
            }
        }
        finish()
    }
}
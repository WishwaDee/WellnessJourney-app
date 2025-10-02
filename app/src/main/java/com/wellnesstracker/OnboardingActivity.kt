package com.wellnesstracker

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.wellnesstracker.adapters.OnboardingAdapter
import com.wellnesstracker.databinding.ActivityOnboardingBinding
import com.wellnesstracker.models.OnboardingPage
import com.wellnesstracker.utils.SessionManager

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        val pages = listOf(
            OnboardingPage(
                title = "Track your wellness",
                description = "Stay on top of your daily habits, moods, and hydration goals in one place.",
                illustration = R.drawable.onboarding_habits
            ),
            OnboardingPage(
                title = "Understand your emotions",
                description = "Journal your moods with helpful prompts and discover daily patterns.",
                illustration = R.drawable.onboarding_mood
            ),
            OnboardingPage(
                title = "Build lasting routines",
                description = "Celebrate streaks, get hydration tips, and keep your routine consistent.",
                illustration = R.drawable.onboarding_hydration
            )
        )

        val onboardingAdapter = OnboardingAdapter(pages)
        binding.viewPager.adapter = onboardingAdapter
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateButtonText(position, pages.size)
            }
        })

        TabLayoutMediator(binding.tabIndicator, binding.viewPager) { _, _ -> }.attach()

        binding.buttonNext.setOnClickListener {
            val nextIndex = binding.viewPager.currentItem + 1
            if (nextIndex < pages.size) {
                binding.viewPager.currentItem = nextIndex
            } else {
                completeOnboarding()
            }
        }

        binding.buttonSkip.setOnClickListener {
            completeOnboarding()
        }
    }

    private fun updateButtonText(position: Int, totalPages: Int) {
        if (position == totalPages - 1) {
            binding.buttonNext.text = getString(R.string.get_started)
        } else {
            binding.buttonNext.text = getString(R.string.next)
        }
    }

    private fun completeOnboarding() {
        sessionManager.setOnboardingCompleted(true)
        val nextActivity = when {
            sessionManager.isLoggedIn() -> MainActivity::class.java
            sessionManager.getUser() != null -> LoginActivity::class.java
            else -> SignupActivity::class.java
        }
        startActivity(Intent(this, nextActivity))
        finish()
    }
}
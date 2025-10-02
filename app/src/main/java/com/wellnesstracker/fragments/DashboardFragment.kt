package com.wellnesstracker.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.wellnesstracker.MainActivity
import com.wellnesstracker.R
import com.wellnesstracker.databinding.FragmentDashboardBinding
import com.wellnesstracker.utils.DataManager
import com.wellnesstracker.utils.SessionManager
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var dataManager: DataManager
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataManager = DataManager(requireContext())
        sessionManager = SessionManager(requireContext())

        setupQuickActions()
        updateDashboard()
    }

    override fun onResume() {
        super.onResume()
        updateDashboard()
    }

    private fun setupQuickActions() {
        binding.cardQuickMood.setOnClickListener { openTab(R.id.nav_mood) }
        binding.cardQuickWater.setOnClickListener { openTab(R.id.nav_hydration) }
        binding.cardQuickHabits.setOnClickListener { openTab(R.id.nav_habits) }

        binding.buttonResetOnboarding.setOnClickListener {
            sessionManager.resetOnboarding()
            Toast.makeText(
                requireContext(),
                R.string.onboarding_reset_message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun openTab(tabId: Int) {
        (activity as? MainActivity)?.openTab(tabId)
    }

    private fun updateDashboard() {
        updateHeader()
        updateHabitSummary()
        updateHydrationSummary()
        updateMoodSummary()
        updateStreak()
    }

    private fun updateHeader() {
        val dateFormat = SimpleDateFormat("EEEE, MMM d", Locale.getDefault())
        binding.textTitle.text = getString(R.string.dashboard_title)
        binding.textDate.text = dateFormat.format(Calendar.getInstance().time)
    }

    private fun updateHabitSummary() {
        val habits = dataManager.getHabits()
        val completedToday = dataManager.getCompletedHabitsCount()
        val percentage = dataManager.getTodayCompletionPercentage()

        binding.textHabitsProgress.text = getString(R.string.format_percentage, percentage)
        binding.progressHabits.progress = percentage

        if (habits.isEmpty()) {
            binding.textHabitsRemaining.text = getString(R.string.no_habits_subtitle)
            binding.textHabitsStat.text = getString(R.string.format_habits_count, 0, 0)
        } else {
            binding.textHabitsRemaining.text = getString(
                R.string.format_habits_remaining,
                completedToday,
                habits.size
            )
            binding.textHabitsStat.text = getString(
                R.string.format_habits_count,
                completedToday,
                habits.size
            )
        }
    }

    private fun updateHydrationSummary() {
        val goal = dataManager.getHydrationGoal()
        val total = dataManager.getTodayHydrationTotal()
        val percentage = dataManager.getHydrationProgressPercentage()
        val remaining = (goal - total).coerceAtLeast(0)

        binding.textHydrationProgress.text = getString(R.string.format_percentage, percentage)
        binding.progressHydration.progress = percentage
        binding.textHydrationRemaining.text = getString(R.string.format_hydration_remaining, remaining)
        binding.textWaterStat.text = getString(R.string.format_progress_value, total, goal)
    }

    private fun updateMoodSummary() {
        val todayMood = dataManager.getLatestMoodForDate()
        if (todayMood == null) {
            binding.textMoodStat.text = getString(R.string.mood_stat_empty)
        } else {
            binding.textMoodStat.text = getString(
                R.string.format_mood_stat,
                todayMood.emoji,
                todayMood.moodName
            )
        }
    }

    private fun updateStreak() {
        val streak = dataManager.getHabitStreak()
        binding.textStreakStat.text = getString(R.string.format_streak_days, streak)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

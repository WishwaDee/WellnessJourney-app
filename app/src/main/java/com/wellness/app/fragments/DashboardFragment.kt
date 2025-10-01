package com.wellness.app.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.wellness.app.R
import com.wellness.app.models.MoodEntry
import com.wellness.app.utils.DataManager
import com.wellness.app.utils.HabitSummary
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class DashboardFragment : Fragment() {

    private lateinit var dataManager: DataManager
    private lateinit var dateText: TextView
    private lateinit var habitsProgressIndicator: LinearProgressIndicator
    private lateinit var habitsProgressLabel: TextView
    private lateinit var hydrationProgressIndicator: LinearProgressIndicator
    private lateinit var hydrationProgressLabel: TextView
    private lateinit var moodEmojiText: TextView
    private lateinit var moodLabelText: TextView
    private lateinit var waterIntakeText: TextView
    private lateinit var habitsDoneText: TextView
    private lateinit var streakText: TextView
    private lateinit var resetButton: MaterialButton
    private lateinit var statMoodValue: TextView
    private lateinit var statWaterValue: TextView
    private lateinit var statHabitsValue: TextView
    private lateinit var statStreakValue: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataManager = DataManager(requireContext())

        dateText = view.findViewById(R.id.dashboardDate)
        habitsProgressIndicator = view.findViewById(R.id.dashboardHabitsProgress)
        habitsProgressLabel = view.findViewById(R.id.dashboardHabitsProgressLabel)
        hydrationProgressIndicator = view.findViewById(R.id.dashboardHydrationProgress)
        hydrationProgressLabel = view.findViewById(R.id.dashboardHydrationProgressLabel)
        moodEmojiText = view.findViewById(R.id.dashboardMoodEmoji)
        moodLabelText = view.findViewById(R.id.dashboardMoodLabel)
        waterIntakeText = view.findViewById(R.id.dashboardWaterText)
        habitsDoneText = view.findViewById(R.id.dashboardHabitsDoneText)
        streakText = view.findViewById(R.id.dashboardStreakText)
        resetButton = view.findViewById(R.id.dashboardResetButton)

        statMoodValue = view.findViewById<View>(R.id.statMood).findViewById(R.id.statValue)
        statWaterValue = view.findViewById<View>(R.id.statWater).findViewById(R.id.statValue)
        statHabitsValue = view.findViewById<View>(R.id.statHabits).findViewById(R.id.statValue)
        statStreakValue = view.findViewById<View>(R.id.statStreak).findViewById(R.id.statValue)

        view.findViewById<View>(R.id.statMood).findViewById<TextView>(R.id.statTitle).text =
            getString(R.string.dashboard_stat_mood)
        view.findViewById<View>(R.id.statWater).findViewById<TextView>(R.id.statTitle).text =
            getString(R.string.dashboard_stat_water)
        view.findViewById<View>(R.id.statHabits).findViewById<TextView>(R.id.statTitle).text =
            getString(R.string.dashboard_stat_habits)
        view.findViewById<View>(R.id.statStreak).findViewById<TextView>(R.id.statTitle).text =
            getString(R.string.dashboard_stat_streak)

        resetButton.setOnClickListener {
            dataManager.resetAllData()
            refreshDashboard()
        }

        refreshDashboard()
    }

    private fun refreshDashboard() {
        dateText.text = getFormattedDate()

        val habitSummary: HabitSummary = dataManager.getHabitSummary()
        val hydrationGoal = dataManager.getHydrationGoal()
        val hydrationToday = dataManager.getTodayHydration()
        val hydrationPercent = dataManager.getHydrationCompletionPercentage()
        val streak = dataManager.getHabitStreak()

        val percent = habitSummary.completionPercentage.coerceIn(0f, 100f).toInt()
        habitsProgressIndicator.progress = percent
        habitsProgressLabel.text = getString(
            R.string.dashboard_habit_progress_format,
            habitSummary.completedCount,
            habitSummary.totalHabits
        )

        hydrationProgressIndicator.progress = hydrationPercent
        hydrationProgressLabel.text = getString(
            R.string.dashboard_hydration_progress_format,
            hydrationToday,
            hydrationGoal
        )

        val latestMood: MoodEntry? = dataManager.getMoodEntries().maxByOrNull { it.timestamp }
        moodEmojiText.text = latestMood?.emoji ?: "🙂"
        moodLabelText.text = latestMood?.moodName ?: getString(R.string.dashboard_mood_empty)

        waterIntakeText.text = getString(
            R.string.dashboard_water_stat,
            hydrationToday
        )

        habitsDoneText.text = getString(
            R.string.dashboard_habits_stat,
            habitSummary.completedCount,
            habitSummary.totalHabits
        )

        streakText.text = resources.getQuantityString(
            R.plurals.habit_streak,
            streak,
            streak
        )

        statMoodValue.text = latestMood?.emoji ?: "🙂"
        statWaterValue.text = getString(R.string.dashboard_water_short, hydrationToday)
        statHabitsValue.text = getString(
            R.string.dashboard_habits_short,
            habitSummary.completedCount,
            habitSummary.totalHabits
        )
        statStreakValue.text = resources.getQuantityString(
            R.plurals.habit_streak_short,
            streak,
            streak
        )
    }

    private fun getFormattedDate(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            formatDateModern()
        } else {
            formatDateLegacy()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun formatDateModern(): String {
        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy", Locale.getDefault())
        return today.format(formatter)
    }

    private fun formatDateLegacy(): String {
        val formatter = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault())
        return formatter.format(Date())
    }
}

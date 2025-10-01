package com.yourcompany.wellnessjourney.ui // Your actual package name (likely includes .ui)


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast

import androidx.fragment.app.Fragment
import com.yourcompany.wellnessjourney.R
import com.yourcompany.wellnessjourney.MainActivity
import com.yourcompany.wellnessjourney.data.Habit
import com.yourcompany.wellnessjourney.data.HabitManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DashboardFragment : Fragment(R.layout.fragment_dashboard)
 {

    private lateinit var textCurrentDate: TextView
    private lateinit var progressHabits: ProgressBar
    private lateinit var textHabitsProgressPercent: TextView
    private lateinit var progressHydration: ProgressBar
    private lateinit var textHydrationProgressPercent: TextView
    private lateinit var textTodayMood: TextView
    private lateinit var textWaterIntake: TextView
    private lateinit var textHabitsDoneCount: TextView
    private lateinit var textStreakCount: TextView
    private lateinit var btnResetOnboarding: Button
    private lateinit var layoutTodaysHabitsList: LinearLayout
    private lateinit var textNoHabitsPlaceholder: TextView

    // For habit management
    private lateinit var habitManager: HabitManager


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        // Initialize UI elements
        textCurrentDate = view.findViewById(R.id.text_current_date)
        progressHabits = view.findViewById(R.id.progress_habits)
        textHabitsProgressPercent = view.findViewById(R.id.text_habits_progress_percent)
        progressHydration = view.findViewById(R.id.progress_hydration)
        textHydrationProgressPercent = view.findViewById(R.id.text_hydration_progress_percent)
        textTodayMood = view.findViewById(R.id.text_today_mood)
        textWaterIntake = view.findViewById(R.id.text_water_intake)
        textHabitsDoneCount = view.findViewById(R.id.text_habits_done_count)
        textStreakCount = view.findViewById(R.id.text_streak_count)
        btnResetOnboarding = view.findViewById(R.id.btn_reset_onboarding)
        layoutTodaysHabitsList = view.findViewById(R.id.layout_todays_habits_list)
        textNoHabitsPlaceholder = view.findViewById(R.id.text_no_habits_placeholder)

        // Initialize HabitManager
        habitManager = HabitManager(requireContext()) // Use requireContext() as Fragment might not have context directly

        // Set current date
        setCurrentDate()

        // Make reset button visible for debugging (can remove later)
        btnResetOnboarding.visibility = View.VISIBLE
        btnResetOnboarding.setOnClickListener {
            habitManager.clearAllHabitsData() // Clear all habit data
            updateDashboard() // Refresh dashboard
            (requireActivity() as MainActivity).getNavController().navigate(R.id.habitsFragment) // Navigate to habits to encourage adding new ones
            Toast.makeText(context, "All habits reset!", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        updateDashboard() // Update dashboard data whenever the fragment is resumed
    }

    private fun setCurrentDate() {
        val dateFormat = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault())
        val currentDate = dateFormat.format(Date())
        textCurrentDate.text = currentDate
    }

    private fun updateDashboard() {
        val todaysHabits = habitManager.getHabitsForToday()

        val totalHabits = todaysHabits.size
        val completedHabits = todaysHabits.count { it.isCompleted }

        val habitsProgressPercent = if (totalHabits > 0) {
            (completedHabits.toFloat() / totalHabits * 100).toInt()
        } else 0

        // Placeholder for hydration logic - we'll implement this later
        val currentWaterIntake = 0 // Will get from HydrationManager
        val targetWaterIntake = 2000 // Example
        val hydrationProgressPercent = if (targetWaterIntake > 0) {
            (currentWaterIntake.toFloat() / targetWaterIntake * 100).toInt()
        } else 0

        // Placeholder for mood - will get from MoodManager
        val todayMood = "N/A" // "Happy"


        updateDashboardUI(
            habitsProgress = habitsProgressPercent,
            hydrationProgress = hydrationProgressPercent,
            waterIntakeMl = currentWaterIntake,
            habitsDoneCount = "$completedHabits/$totalHabits",
            mood = todayMood,
            streak = 0 // Implement streak logic later
        )

        updateTodaysHabitsList(todaysHabits)
    }

    // This method updates the UI elements with calculated data
    private fun updateDashboardUI(
        habitsProgress: Int,
        hydrationProgress: Int,
        waterIntakeMl: Int,
        habitsDoneCount: String,
        mood: String,
        streak: Int
    ) {
        progressHabits.progress = habitsProgress
        textHabitsProgressPercent.text = "$habitsProgress%"

        progressHydration.progress = hydrationProgress
        textHydrationProgressPercent.text = "$hydrationProgress%"

        textTodayMood.text = mood // You can make this "Today's Mood: $mood"
        textWaterIntake.text = "${waterIntakeMl}ml"
        textHabitsDoneCount.text = habitsDoneCount
        textStreakCount.text = "$streak Days"
    }

    // This method dynamically adds habit items to the dashboard
    private fun updateTodaysHabitsList(habits: List<Habit>) {
        layoutTodaysHabitsList.removeAllViews() // Clear existing views

        // Remove the static placeholder item_habit if it was previously included.
        // The if (habits.isEmpty()) block below now manages textNoHabitsPlaceholder.

        if (habits.isEmpty()) {
            textNoHabitsPlaceholder.visibility = View.VISIBLE
        } else {
            textNoHabitsPlaceholder.visibility = View.GONE
            // Dynamically add views for each habit
            habits.forEach { habit ->
                val habitItemView = LayoutInflater.from(context).inflate(R.layout.item_habit, layoutTodaysHabitsList, false)
                val habitIcon: ImageView = habitItemView.findViewById(R.id.habit_icon)
                val habitName: TextView = habitItemView.findViewById(R.id.habit_name)
                val habitGoal: TextView = habitItemView.findViewById(R.id.habit_goal)
                val habitCompletionCheckbox: CheckBox = habitItemView.findViewById(R.id.habit_completion_checkbox)

                if (habit.iconResId != 0) {
                    habitIcon.setImageResource(habit.iconResId)
                } else {
                    habitIcon.setImageResource(R.drawable.ic_habits) // Default icon
                }
                habitName.text = habit.name
                habitGoal.text = "Goal: ${habit.goal}"
                habitCompletionCheckbox.isChecked = habit.isCompleted

                habitItemView.setOnClickListener {
                    // Navigate to Habits fragment or open edit dialog
                    // For simplicity, let's navigate to HabitsFragment to manage them
                    (requireActivity() as MainActivity).getNavController().navigate(R.id.habitsFragment)
                }

                habitCompletionCheckbox.setOnCheckedChangeListener { _, isChecked ->
                    // Update habit completion state
                    habitManager.updateHabitCompletion(habit, isChecked, habit.completionValue) // Assuming completionValue is simple check here
                    updateDashboard() // Refresh dashboard instantly
                }

                layoutTodaysHabitsList.addView(habitItemView)
            }
        }
    }
}


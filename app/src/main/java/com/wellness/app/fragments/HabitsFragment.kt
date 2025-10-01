package com.wellness.app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import com.wellness.app.R
import com.wellness.app.adapters.HabitAdapter
import com.wellness.app.models.Habit
import com.wellness.app.utils.DataManager
import com.wellness.app.utils.HabitSummary
import java.util.UUID

class HabitsFragment : Fragment() {

    private lateinit var dataManager: DataManager
    private lateinit var habitAdapter: HabitAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var fabAddHabit: ExtendedFloatingActionButton
    private lateinit var percentText: TextView
    private lateinit var completedCountText: TextView
    private lateinit var remainingText: TextView
    private lateinit var completedLabel: TextView
    private lateinit var streakLabel: TextView
    private lateinit var progressIndicator: LinearProgressIndicator
    private val habits = mutableListOf<Habit>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_habits, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataManager = DataManager(requireContext())

        recyclerView = view.findViewById(R.id.habitsRecyclerView)
        fabAddHabit = view.findViewById(R.id.fabAddHabit)
        percentText = view.findViewById(R.id.habitsProgressPercent)
        completedCountText = view.findViewById(R.id.habitsCompletedCount)
        remainingText = view.findViewById(R.id.habitsRemainingText)
        completedLabel = view.findViewById(R.id.habitsCompletedLabel)
        streakLabel = view.findViewById(R.id.habitsStreakLabel)
        progressIndicator = view.findViewById(R.id.habitsProgressIndicator)

        setupRecyclerView()
        loadHabits()

        fabAddHabit.setOnClickListener {
            showAddHabitDialog()
        }
    }

    private fun setupRecyclerView() {
        habitAdapter = HabitAdapter(
            habits,
            onHabitClick = { habit -> showEditHabitDialog(habit) },
            onHabitLongClick = { habit -> showDeleteHabitDialog(habit) },
            onHabitUpdated = { habit ->
                dataManager.updateHabit(habit)
                updateSummary()
            }
        )

        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = habitAdapter
        }
    }

    private fun loadHabits() {
        habits.clear()
        habits.addAll(dataManager.getHabits().sortedBy { it.name })
        habitAdapter.notifyDataSetChanged()
        updateSummary()
    }

    private fun saveHabits() {
        dataManager.saveHabits(habits)
        updateSummary()
    }

    private fun updateSummary() {
        val summary: HabitSummary = dataManager.getHabitSummary()
        val percent = summary.completionPercentage.coerceIn(0f, 100f).toInt()
        val remaining = (summary.totalHabits - summary.completedCount).coerceAtLeast(0)
        val streak = dataManager.getHabitStreak()

        percentText.text = getString(R.string.habit_percent_format, percent)
        completedCountText.text = getString(
            R.string.habit_completed_count_format,
            summary.completedCount,
            summary.totalHabits
        )
        remainingText.text = resources.getQuantityString(
            R.plurals.habits_remaining,
            remaining,
            remaining
        )
        completedLabel.text = resources.getQuantityString(
            R.plurals.habits_completed,
            summary.completedCount,
            summary.completedCount
        )
        streakLabel.text = resources.getQuantityString(
            R.plurals.habit_streak,
            streak,
            streak
        )
        progressIndicator.progress = percent
    }

    private fun showAddHabitDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_habit, null)
        val nameInput = dialogView.findViewById<TextInputEditText>(R.id.habitNameInput)
        val descInput = dialogView.findViewById<TextInputEditText>(R.id.habitDescriptionInput)
        val goalInput = dialogView.findViewById<TextInputEditText>(R.id.habitGoalInput)
        val unitInput = dialogView.findViewById<TextInputEditText>(R.id.habitUnitInput)
        val stepInput = dialogView.findViewById<TextInputEditText>(R.id.habitStepInput)
        val iconInput = dialogView.findViewById<TextInputEditText>(R.id.habitIconInput)

        unitInput?.setText(getString(R.string.default_habit_unit))
        goalInput?.setText("10")
        stepInput?.setText("5")

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.add_habit_title)
            .setView(dialogView)
            .setPositiveButton(R.string.action_add) { _, _ ->
                val name = nameInput?.text?.toString()?.trim().orEmpty()
                val description = descInput?.text?.toString()?.trim().orEmpty()
                val goal = goalInput?.text?.toString()?.toIntOrNull() ?: 0
                val unit = unitInput?.text?.toString().orEmpty().trim().ifEmpty {
                    getString(R.string.default_habit_unit)
                }
                val step = stepInput?.text?.toString()?.toIntOrNull() ?: 1
                val icon = iconInput?.text?.toString().orEmpty().trim().ifEmpty { "✨" }

                if (name.isNotEmpty() && goal > 0) {
                    val habit = Habit(
                        id = UUID.randomUUID().toString(),
                        name = name,
                        description = description,
                        goal = goal,
                        unit = unit,
                        step = step.coerceAtLeast(1),
                        icon = icon,
                        color = HabitColorPalette.nextColor()
                    )
                    habits.add(habit)
                    saveHabits()
                    loadHabits()
                }
            }
            .setNegativeButton(R.string.action_cancel, null)
            .show()
    }

    private fun showEditHabitDialog(habit: Habit) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_habit, null)
        val nameInput = dialogView.findViewById<TextInputEditText>(R.id.habitNameInput)
        val descInput = dialogView.findViewById<TextInputEditText>(R.id.habitDescriptionInput)
        val goalInput = dialogView.findViewById<TextInputEditText>(R.id.habitGoalInput)
        val unitInput = dialogView.findViewById<TextInputEditText>(R.id.habitUnitInput)
        val stepInput = dialogView.findViewById<TextInputEditText>(R.id.habitStepInput)
        val iconInput = dialogView.findViewById<TextInputEditText>(R.id.habitIconInput)

        nameInput?.setText(habit.name)
        descInput?.setText(habit.description)
        goalInput?.setText(habit.goal.toString())
        unitInput?.setText(habit.unit)
        stepInput?.setText(habit.step.toString())
        iconInput?.setText(habit.icon)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.edit_habit_title)
            .setView(dialogView)
            .setPositiveButton(R.string.action_save) { _, _ ->
                habit.name = nameInput?.text?.toString()?.trim().orEmpty()
                habit.description = descInput?.text?.toString()?.trim().orEmpty()
                habit.goal = goalInput?.text?.toString()?.toIntOrNull() ?: habit.goal
                habit.unit = unitInput?.text?.toString().orEmpty().trim().ifEmpty { habit.unit }
                habit.step = stepInput?.text?.toString()?.toIntOrNull()?.coerceAtLeast(1) ?: habit.step
                habit.icon = iconInput?.text?.toString().orEmpty().trim().ifEmpty { habit.icon }
                saveHabits()
                loadHabits()
            }
            .setNegativeButton(R.string.action_cancel, null)
            .show()
    }

    private fun showDeleteHabitDialog(habit: Habit) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.delete_habit_title, habit.name))
            .setMessage(R.string.delete_habit_message)
            .setPositiveButton(R.string.action_delete) { _, _ ->
                val position = habits.indexOf(habit)
                if (position >= 0) {
                    habits.removeAt(position)
                    saveHabits()
                    loadHabits()
                }
            }
            .setNegativeButton(R.string.action_cancel, null)
            .show()
    }

    private object HabitColorPalette {
        private val colors = listOf(
            "#FFE3E3",
            "#E3F2FD",
            "#F3E5F5",
            "#FFF3CD",
            "#E8F5E9",
            "#E1F5FE"
        )

        private var index = 0

        fun nextColor(): String {
            val color = colors[index % colors.size]
            index++
            return color
        }
    }
}

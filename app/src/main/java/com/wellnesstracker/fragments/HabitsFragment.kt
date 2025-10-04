package com.wellnesstracker.fragments

import android.app.AlertDialog
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.wellnesstracker.adapters.HabitAdapter
import com.wellnesstracker.databinding.FragmentHabitsBinding
import com.wellnesstracker.dialogs.AddHabitDialog
import com.wellnesstracker.models.Habit
import com.wellnesstracker.utils.DataManager
import com.wellnesstracker.widgets.HabitWidgetProvider

class HabitsFragment : Fragment() {
    private var _binding: FragmentHabitsBinding? = null
    private val binding get() = _binding!!
    private lateinit var dataManager: DataManager
    private lateinit var habitAdapter: HabitAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHabitsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataManager = DataManager(requireContext())
        setupRecyclerView()
        setupFab()
        updateProgressCard()
    }

    private fun setupRecyclerView() {
        habitAdapter = HabitAdapter(
            habits = dataManager.getHabits(),
            dataManager = dataManager,
            onHabitClick = { habit -> showEditDialog(habit) },
            onHabitComplete = {
                updateProgressCard()
                updateWidget()
            },
            onHabitDelete = { habit -> deleteHabit(habit) }
        )

        binding.recyclerViewHabits.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = habitAdapter
        }

        updateEmptyState()
    }

    private fun setupFab() {
        binding.fabAddHabit.setOnClickListener {
            showAddDialog()
        }
    }

    private fun showAddDialog() {
        AddHabitDialog { name, description, icon ->
            val habit = Habit(name = name, description = description, icon = icon)
            dataManager.addHabit(habit)
            habitAdapter.updateHabits(dataManager.getHabits())
            updateEmptyState()
            updateProgressCard()
            updateWidget()
        }.show(childFragmentManager, "AddHabitDialog")
    }

    private fun showEditDialog(habit: Habit) {
        AddHabitDialog(habit) { name, description, icon ->
            val updatedHabit = habit.copy(name = name, description = description, icon = icon)
            dataManager.updateHabit(updatedHabit)
            habitAdapter.updateHabits(dataManager.getHabits())
            updateWidget()
        }.show(childFragmentManager, "EditHabitDialog")
    }

    private fun deleteHabit(habit: Habit) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Habit")
            .setMessage("Are you sure you want to delete '${habit.name}'?")
            .setPositiveButton("Delete") { _, _ ->
                dataManager.deleteHabit(habit.id)
                habitAdapter.updateHabits(dataManager.getHabits())
                updateEmptyState()
                updateProgressCard()
                updateWidget()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun updateProgressCard() {
        val percentage = dataManager.getTodayCompletionPercentage()
        binding.textProgress.text = "$percentage%"
        binding.progressBar.progress = percentage

        val habits = dataManager.getHabits()
        val completed = habits.count { dataManager.isHabitCompleted(it.id, dataManager.getTodayDate()) }
        binding.textCompletedCount.text = getString(R.string.habit_completed_count, completed)
        val remaining = (habits.size - completed).coerceAtLeast(0)
        binding.textRemainingCount.text = getString(R.string.habit_remaining_count, remaining)

        binding.textProgressDetail.text = if (habits.isEmpty()) {
            getString(R.string.no_habits_subtitle)
        } else {
            getString(R.string.format_habits_remaining, completed, habits.size)
        }
    }

    private fun updateEmptyState() {
        if (dataManager.getHabits().isEmpty()) {
            binding.recyclerViewHabits.visibility = View.GONE
            binding.emptyStateLayout.visibility = View.VISIBLE
        } else {
            binding.recyclerViewHabits.visibility = View.VISIBLE
            binding.emptyStateLayout.visibility = View.GONE
        }
    }

    private fun updateWidget() {
        val intent = Intent(requireContext(), HabitWidgetProvider::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        }
        val ids = AppWidgetManager.getInstance(requireContext())
            .getAppWidgetIds(ComponentName(requireContext(), HabitWidgetProvider::class.java))
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        requireContext().sendBroadcast(intent)
    }

    override fun onResume() {
        super.onResume()
        habitAdapter.updateHabits(dataManager.getHabits())
        updateProgressCard()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
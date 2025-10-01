package com.wellness.tracker.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.wellness.tracker.R
import com.wellness.tracker.adapters.HabitsAdapter
import com.wellness.tracker.data.PreferencesManager
import com.wellness.tracker.databinding.FragmentHabitsBinding
import com.wellness.tracker.models.Habit
import com.wellness.tracker.models.HabitProgress
import java.text.SimpleDateFormat
import java.util.*

class HabitsFragment : Fragment() {

    private var _binding: FragmentHabitsBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var habitsAdapter: HabitsAdapter
    private val habits = mutableListOf<Habit>()
    private val habitProgress = mutableListOf<HabitProgress>()
    private val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

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
        
        preferencesManager = PreferencesManager(requireContext())
        setupRecyclerView()
        loadData()
        setupFab()
        updateProgressDisplay()
    }

    private fun setupRecyclerView() {
        habitsAdapter = HabitsAdapter(habits) { habit, action ->
            when (action) {
                "toggle" -> toggleHabitCompletion(habit)
                "edit" -> editHabit(habit)
                "delete" -> deleteHabit(habit)
            }
        }
        
        binding.recyclerViewHabits.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = habitsAdapter
        }
    }

    private fun setupFab() {
        binding.fabAddHabit.setOnClickListener {
            showAddHabitDialog()
        }
    }

    private fun loadData() {
        habits.clear()
        habits.addAll(preferencesManager.getHabits())
        
        habitProgress.clear()
        habitProgress.addAll(preferencesManager.getHabitProgress())
        
        habitsAdapter.notifyDataSetChanged()
    }

    private fun showAddHabitDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_habit, null)
        val nameEdit = dialogView.findViewById<EditText>(R.id.editTextHabitName)
        val descEdit = dialogView.findViewById<EditText>(R.id.editTextHabitDescription)
        
        AlertDialog.Builder(requireContext())
            .setTitle("Add New Habit")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val name = nameEdit.text.toString().trim()
                val description = descEdit.text.toString().trim()
                
                if (name.isNotEmpty()) {
                    val habit = Habit(name = name, description = description)
                    habits.add(habit)
                    preferencesManager.saveHabits(habits)
                    habitsAdapter.notifyItemInserted(habits.size - 1)
                    updateProgressDisplay()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun editHabit(habit: Habit) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_habit, null)
        val nameEdit = dialogView.findViewById<EditText>(R.id.editTextHabitName)
        val descEdit = dialogView.findViewById<EditText>(R.id.editTextHabitDescription)
        
        nameEdit.setText(habit.name)
        descEdit.setText(habit.description)
        
        AlertDialog.Builder(requireContext())
            .setTitle("Edit Habit")
            .setView(dialogView)
            .setPositiveButton("Update") { _, _ ->
                val name = nameEdit.text.toString().trim()
                val description = descEdit.text.toString().trim()
                
                if (name.isNotEmpty()) {
                    val index = habits.indexOfFirst { it.id == habit.id }
                    if (index != -1) {
                        habits[index] = habit.copy(name = name, description = description)
                        preferencesManager.saveHabits(habits)
                        habitsAdapter.notifyItemChanged(index)
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteHabit(habit: Habit) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Habit")
            .setMessage("Are you sure you want to delete this habit?")
            .setPositiveButton("Delete") { _, _ ->
                val index = habits.indexOfFirst { it.id == habit.id }
                if (index != -1) {
                    habits.removeAt(index)
                    
                    // Remove associated progress
                    habitProgress.removeAll { it.habitId == habit.id }
                    
                    preferencesManager.saveHabits(habits)
                    preferencesManager.saveHabitProgress(habitProgress)
                    
                    habitsAdapter.notifyItemRemoved(index)
                    updateProgressDisplay()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun toggleHabitCompletion(habit: Habit) {
        val existingProgress = habitProgress.find { it.habitId == habit.id && it.date == today }
        
        if (existingProgress != null) {
            val index = habitProgress.indexOf(existingProgress)
            habitProgress[index] = existingProgress.copy(
                currentValue = if (existingProgress.isCompleted) 0 else habit.targetValue,
                isCompleted = !existingProgress.isCompleted
            )
        } else {
            habitProgress.add(
                HabitProgress(
                    habitId = habit.id,
                    date = today,
                    currentValue = habit.targetValue,
                    isCompleted = true
                )
            )
        }
        
        preferencesManager.saveHabitProgress(habitProgress)
        habitsAdapter.notifyDataSetChanged()
        updateProgressDisplay()
    }

    private fun updateProgressDisplay() {
        val totalHabits = habits.size
        val completedHabits = habits.count { habit ->
            habitProgress.any { it.habitId == habit.id && it.date == today && it.isCompleted }
        }
        
        val percentage = if (totalHabits > 0) (completedHabits * 100) / totalHabits else 0
        
        binding.textViewProgress.text = "Today's Progress: $completedHabits/$totalHabits ($percentage%)"
        binding.progressBar.progress = percentage
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
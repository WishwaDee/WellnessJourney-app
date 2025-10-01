package com.yourcompany.wellnessjourney.ui // Your actual package name (likely includes .ui)


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.yourcompany.wellnessjourney.R
import com.yourcompany.wellnessjourney.adapters.HabitAdapter
import com.yourcompany.wellnessjourney.data.Habit
import com.yourcompany.wellnessjourney.data.HabitManager // We'll create this soon!
import java.util.UUID

class HabitsFragment : Fragment(R.layout.fragment_habits)
{

    private lateinit var recyclerViewHabits: RecyclerView
    private lateinit var textNoHabits: TextView
    private lateinit var fabAddHabit: FloatingActionButton
    private lateinit var habitAdapter: HabitAdapter
    private lateinit var habitManager: HabitManager // Will initialize later

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_habits, container, false)

        recyclerViewHabits = view.findViewById(R.id.recycler_view_habits)
        textNoHabits = view.findViewById(R.id.text_no_habits)
        fabAddHabit = view.findViewById(R.id.fab_add_habit)

        // Initialize HabitManager
        habitManager = HabitManager(requireContext())

        setupRecyclerView()

        fabAddHabit.setOnClickListener {
            showAddEditHabitDialog()
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        // Reload habits whenever the fragment resumes (e.g., coming back from another tab)
        loadHabits()
    }

    private fun setupRecyclerView() {
        habitAdapter = HabitAdapter(
            mutableListOf(), // Initial empty list
            onHabitClickListener = { habit ->
                // Handle habit click for editing
                showAddEditHabitDialog(habit)
            },
            onHabitCheckListener = { habit, isChecked, completionValue ->
                // Handle habit checkbox check/uncheck
                habit.isCompleted = isChecked
                habit.completionValue = completionValue // Update value if needed
                habitManager.updateHabit(habit)
                // You might want to update the dashboard here too
            }
        )
        recyclerViewHabits.layoutManager = LinearLayoutManager(context)
        recyclerViewHabits.adapter = habitAdapter
    }

    private fun loadHabits() {
        val habits = habitManager.getHabitsForToday() // Or getAllHabits() initially
        habitAdapter.updateHabits(habits)
        updateEmptyView(habits.isEmpty())
    }

    private fun updateEmptyView(isEmpty: Boolean) {
        if (isEmpty) {
            textNoHabits.visibility = View.VISIBLE
            recyclerViewHabits.visibility = View.GONE
        } else {
            textNoHabits.visibility = View.GONE
            recyclerViewHabits.visibility = View.VISIBLE
        }
    }

    private fun showAddEditHabitDialog(habitToEdit: Habit? = null) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_edit_habit, null)
        val builder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle(if (habitToEdit == null) "Add New Habit" else "Edit Habit")

        val dialog = builder.create()

        val editHabitName: EditText = dialogView.findViewById(R.id.edit_habit_name)
        val editHabitGoal: EditText = dialogView.findViewById(R.id.edit_habit_goal)
        val spinnerHabitIcon: Spinner = dialogView.findViewById(R.id.spinner_habit_icon)
        val btnCancelHabit: Button = dialogView.findViewById(R.id.btn_cancel_habit)
        val btnSaveHabit: Button = dialogView.findViewById(R.id.btn_save_habit)

        // Setup spinner adapter
        val iconNames = resources.getStringArray(R.array.habit_icons_array)
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, iconNames)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerHabitIcon.adapter = spinnerAdapter

        // If editing an existing habit, pre-fill data
        if (habitToEdit != null) {
            editHabitName.setText(habitToEdit.name)
            editHabitGoal.setText(habitToEdit.goal)
            // Select the correct icon in the spinner if habitToEdit.iconResId is set
            val iconResIds = getIconDrawableMap().values.toList()
            val selectedIconIndex = iconResIds.indexOf(habitToEdit.iconResId)
            if (selectedIconIndex != -1) {
                spinnerHabitIcon.setSelection(selectedIconIndex + 1) // +1 because first item is "No Icon"
            }
        }


        btnCancelHabit.setOnClickListener {
            dialog.dismiss()
        }

        btnSaveHabit.setOnClickListener {
            val name = editHabitName.text.toString().trim()
            val goal = editHabitGoal.text.toString().trim()
            val selectedIconPosition = spinnerHabitIcon.selectedItemPosition
            val selectedIconResId = getIconDrawableMap().values.toTypedArray().getOrNull(selectedIconPosition -1) ?: 0 // Adjust for "No Icon"

            if (name.isEmpty() || goal.isEmpty()) {
                Toast.makeText(context, "Habit name and goal cannot be empty!", Toast.LENGTH_SHORT).show()
            } else {
                if (habitToEdit == null) {
                    // Add new habit
                    val newHabit = Habit(
                        name = name,
                        goal = goal,
                        iconResId = selectedIconResId // Will map this to actual drawables
                    )
                    habitManager.addHabit(newHabit)
                } else {
                    // Update existing habit
                    habitToEdit.name = name
                    habitToEdit.goal = goal
                    habitToEdit.iconResId = selectedIconResId
                    habitManager.updateHabit(habitToEdit)
                }
                loadHabits() // Reload and refresh the list
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    // Helper function to map spinner text to actual drawable resource IDs
    private fun getIconDrawableMap(): Map<String, Int> {
        return mapOf(
            "Exercise Icon" to R.drawable.ic_habits, // Use a generic habit icon for now
            "Water Icon" to R.drawable.ic_hydration,
            "Read Icon" to R.drawable.ic_small_target, // Placeholder for read icon
            "Sleep Icon" to R.drawable.ic_small_star, // Placeholder for sleep icon
            "Meditation Icon" to R.drawable.ic_mood, // Placeholder for meditation icon
            // Add more mappings as you add custom icons
        )
    }
}

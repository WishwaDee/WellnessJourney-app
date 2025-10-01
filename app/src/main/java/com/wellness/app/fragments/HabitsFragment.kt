package com.wellness.app.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.wellness.app.R
import com.wellness.app.adapters.HabitAdapter
import com.wellness.app.models.Habit
import com.wellness.app.utils.DataManager
import java.util.*

class HabitsFragment : Fragment() {

    private lateinit var dataManager: DataManager
    private lateinit var habitAdapter: HabitAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var fabAddHabit: FloatingActionButton
    private val habits = mutableListOf<Habit>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_habits, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataManager = DataManager(requireContext())

        recyclerView = view.findViewById(R.id.habitsRecyclerView)
        fabAddHabit = view.findViewById(R.id.fabAddHabit)

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
            onToggleComplete = { saveHabits() }
        )

        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = habitAdapter
        }
    }

    private fun loadHabits() {
        habits.clear()
        habits.addAll(dataManager.getHabits())
        habitAdapter.notifyDataSetChanged()
    }

    private fun saveHabits() {
        dataManager.saveHabits(habits)
    }

    private fun showAddHabitDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_habit, null)
        val nameInput = dialogView.findViewById<EditText>(R.id.habitNameInput)
        val descInput = dialogView.findViewById<EditText>(R.id.habitDescriptionInput)

        AlertDialog.Builder(requireContext())
            .setTitle("Add New Habit")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val name = nameInput.text.toString().trim()
                val description = descInput.text.toString().trim()

                if (name.isNotEmpty()) {
                    val habit = Habit(
                        id = UUID.randomUUID().toString(),
                        name = name,
                        description = description
                    )
                    habits.add(habit)
                    saveHabits()
                    habitAdapter.notifyItemInserted(habits.size - 1)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showEditHabitDialog(habit: Habit) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_habit, null)
        val nameInput = dialogView.findViewById<EditText>(R.id.habitNameInput)
        val descInput = dialogView.findViewById<EditText>(R.id.habitDescriptionInput)

        nameInput.setText(habit.name)
        descInput.setText(habit.description)

        AlertDialog.Builder(requireContext())
            .setTitle("Edit Habit")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                habit.name = nameInput.text.toString().trim()
                habit.description = descInput.text.toString().trim()
                saveHabits()
                habitAdapter.notifyDataSetChanged()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDeleteHabitDialog(habit: Habit) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Habit")
            .setMessage("Are you sure you want to delete '${habit.name}'?")
            .setPositiveButton("Delete") { _, _ ->
                val position = habits.indexOf(habit)
                habits.remove(habit)
                saveHabits()
                habitAdapter.notifyItemRemoved(position)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}

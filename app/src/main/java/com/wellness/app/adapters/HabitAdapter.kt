package com.wellness.app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wellness.app.R
import com.wellness.app.models.Habit

class HabitAdapter(
    private val habits: MutableList<Habit>,
    private val onHabitClick: (Habit) -> Unit,
    private val onHabitLongClick: (Habit) -> Unit,
    private val onToggleComplete: (Habit) -> Unit
) : RecyclerView.Adapter<HabitAdapter.HabitViewHolder>() {

    class HabitViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val checkBox: CheckBox = view.findViewById(R.id.habitCheckBox)
        val habitName: TextView = view.findViewById(R.id.habitName)
        val habitDescription: TextView = view.findViewById(R.id.habitDescription)
        val progressBar: ProgressBar = view.findViewById(R.id.habitProgress)
        val progressText: TextView = view.findViewById(R.id.habitProgressText)
        val editButton: ImageButton = view.findViewById(R.id.editButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_habit, parent, false)
        return HabitViewHolder(view)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        val habit = habits[position]

        holder.habitName.text = habit.name
        holder.habitDescription.text = habit.description
        holder.checkBox.isChecked = habit.isCompletedToday()

        val weekProgress = habit.getCompletionPercentageThisWeek()
        holder.progressBar.progress = weekProgress.toInt()
        holder.progressText.text = "${weekProgress.toInt()}%"

        holder.checkBox.setOnClickListener {
            habit.toggleCompletion()
            onToggleComplete(habit)
            notifyItemChanged(position)
        }

        holder.itemView.setOnClickListener {
            onHabitClick(habit)
        }

        holder.itemView.setOnLongClickListener {
            onHabitLongClick(habit)
            true
        }

        holder.editButton.setOnClickListener {
            onHabitClick(habit)
        }
    }

    override fun getItemCount() = habits.size

    fun updateHabits(newHabits: List<Habit>) {
        habits.clear()
        habits.addAll(newHabits)
        notifyDataSetChanged()
    }
}

package com.yourcompany.wellnessjourney.adapters // Make sure this matches your package structure

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yourcompany.wellnessjourney.R
import com.yourcompany.wellnessjourney.data.Habit

class HabitAdapter(
    private val habits: MutableList<Habit>,
    private val onHabitClickListener: (Habit) -> Unit, // For editing
    private val onHabitCheckListener: (Habit, Boolean, Int) -> Unit // For completing/tracking progress
) : RecyclerView.Adapter<HabitAdapter.HabitViewHolder>() {

    inner class HabitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val habitIcon: ImageView = itemView.findViewById(R.id.habit_icon)
        val habitName: TextView = itemView.findViewById(R.id.habit_name)
        val habitGoal: TextView = itemView.findViewById(R.id.habit_goal)
        val habitCompletionCheckbox: CheckBox = itemView.findViewById(R.id.habit_completion_checkbox)

        init {
            itemView.setOnClickListener {
                onHabitClickListener(habits[adapterPosition])
            }
            habitCompletionCheckbox.setOnClickListener {
                val habit = habits[adapterPosition]
                // For a simple checkbox, completionValue might just be 100% or 1 unit.
                // For more complex tracking (e.g., water intake), we'll need a different UI element.
                onHabitCheckListener(habit, habitCompletionCheckbox.isChecked, 1)
            }
        }

        fun bind(habit: Habit) {
            if (habit.iconResId != 0) {
                habitIcon.setImageResource(habit.iconResId)
                habitIcon.visibility = View.VISIBLE
            } else {
                // Set a default icon or hide if no specific icon
                habitIcon.setImageResource(R.drawable.ic_habits) // A default habit icon
                habitIcon.visibility = View.VISIBLE
            }
            habitName.text = habit.name
            habitGoal.text = "Goal: ${habit.goal}"
            habitCompletionCheckbox.isChecked = habit.isCompleted
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_habit, parent, false)
        return HabitViewHolder(view)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        holder.bind(habits[position])
    }

    override fun getItemCount(): Int = habits.size

    // Call this method when the underlying data changes
    fun updateHabits(newHabits: List<Habit>) {
        habits.clear()
        habits.addAll(newHabits)
        notifyDataSetChanged()
    }
}
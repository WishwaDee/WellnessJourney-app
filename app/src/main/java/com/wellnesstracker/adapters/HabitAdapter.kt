package com.wellnesstracker.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wellnesstracker.R
import com.wellnesstracker.databinding.ItemHabitBinding
import com.wellnesstracker.models.Habit
import com.wellnesstracker.utils.DataManager

class HabitAdapter(
    private var habits: List<Habit>,
    private val dataManager: DataManager,
    private val onHabitClick: (Habit) -> Unit,
    private val onHabitComplete: () -> Unit,
    private val onHabitDelete: (Habit) -> Unit
) : RecyclerView.Adapter<HabitAdapter.HabitViewHolder>() {

    inner class HabitViewHolder(private val binding: ItemHabitBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(habit: Habit) {
            val today = dataManager.getTodayDate()
            val context = binding.root.context

            binding.textIcon.text = habit.icon
            binding.textHabitName.text = habit.name
            binding.textHabitDescription.text = if (habit.description.isBlank()) {
                context.getString(R.string.habit_description_placeholder)
            } else {
                context.getString(R.string.habit_goal_label, habit.description)
            }

            updateProgressState(dataManager.isHabitCompleted(habit.id, today))

            binding.buttonPlus.setOnClickListener {
                val position = absoluteAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val completed = dataManager.isHabitCompleted(habit.id, today)
                    if (!completed) {
                        dataManager.toggleHabitCompletion(habit.id, today)
                        onHabitComplete()
                        updateProgressState(true)
                        notifyItemChanged(position)
                    }
                }
            }

            binding.buttonMinus.setOnClickListener {
                val position = absoluteAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val completed = dataManager.isHabitCompleted(habit.id, today)
                    if (completed) {
                        dataManager.toggleHabitCompletion(habit.id, today)
                        onHabitComplete()
                        updateProgressState(false)
                        notifyItemChanged(position)
                    }
                }
            }

            binding.root.setOnClickListener {
                onHabitClick(habit)
            }

            binding.root.setOnLongClickListener {
                onHabitDelete(habit)
                true
            }
        }

        private fun updateProgressState(isCompleted: Boolean) {
            binding.progressIndicator.progress = if (isCompleted) 100 else 0
            binding.textProgressValue.text = if (isCompleted) "100%" else "0%"
            val labelRes = if (isCompleted) {
                R.string.habit_status_complete
            } else {
                R.string.habit_status_incomplete
            }
            binding.textCompletionLabel.text = binding.root.context.getString(labelRes)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val binding = ItemHabitBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return HabitViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        holder.bind(habits[position])
    }

    override fun getItemCount() = habits.size

    fun updateHabits(newHabits: List<Habit>) {
        habits = newHabits
        notifyDataSetChanged()
    }
}
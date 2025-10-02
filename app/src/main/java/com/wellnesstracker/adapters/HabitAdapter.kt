package com.wellnesstracker.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
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
            val isCompleted = dataManager.isHabitCompleted(habit.id, today)

            binding.textIcon.text = habit.icon
            binding.textHabitName.text = habit.name
            binding.textHabitDescription.text = habit.description
            binding.checkboxComplete.isChecked = isCompleted

            binding.checkboxComplete.setOnCheckedChangeListener { _, _ ->
                dataManager.toggleHabitCompletion(habit.id, today)
                onHabitComplete()
            }

            binding.root.setOnClickListener {
                onHabitClick(habit)
            }

            binding.root.setOnLongClickListener {
                onHabitDelete(habit)
                true
            }
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
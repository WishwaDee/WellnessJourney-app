package com.wellness.app.adapters

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.wellness.app.R
import com.wellness.app.models.Habit

class HabitAdapter(
    private val habits: MutableList<Habit>,
    private val onHabitClick: (Habit) -> Unit,
    private val onHabitLongClick: (Habit) -> Unit,
    private val onHabitUpdated: (Habit) -> Unit
) : RecyclerView.Adapter<HabitAdapter.HabitViewHolder>() {

    class HabitViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val card: MaterialCardView = view as MaterialCardView
        val iconView: TextView = view.findViewById(R.id.habitIcon)
        val nameView: TextView = view.findViewById(R.id.habitName)
        val descriptionView: TextView = view.findViewById(R.id.habitDescription)
        val goalView: TextView = view.findViewById(R.id.habitGoal)
        val percentView: TextView = view.findViewById(R.id.habitProgressPercent)
        val progressIndicator: LinearProgressIndicator = view.findViewById(R.id.habitProgress)
        val valueView: TextView = view.findViewById(R.id.habitProgressValue)
        val unitView: TextView = view.findViewById(R.id.habitUnit)
        val incrementButton: MaterialButton = view.findViewById(R.id.incrementButton)
        val decrementButton: MaterialButton = view.findViewById(R.id.decrementButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_habit, parent, false)
        return HabitViewHolder(view)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        val habit = habits[position]

        holder.iconView.text = habit.icon
        runCatching { Color.parseColor(habit.color) }.getOrNull()?.let { color ->
            holder.iconView.backgroundTintList = ColorStateList.valueOf(color)
            holder.card.strokeColor = color
        }

        holder.nameView.text = habit.name
        holder.descriptionView.text = habit.description
        holder.goalView.text = holder.goalView.context.getString(
            R.string.habit_goal_format,
            habit.goal,
            habit.unit
        )

        val progress = habit.getTodayProgress()
        val percentage = habit.getCompletionPercentage()

        holder.percentView.text = holder.percentView.context.getString(
            R.string.habit_percent_format,
            percentage
        )
        holder.progressIndicator.progress = percentage
        holder.valueView.text = progress.toString()
        holder.unitView.text = habit.unit

        holder.incrementButton.setOnClickListener {
            val adapterPosition = holder.bindingAdapterPosition
            if (adapterPosition != RecyclerView.NO_POSITION) {
                habit.adjustTodayProgress(habit.step)
                onHabitUpdated(habit)
                notifyItemChanged(adapterPosition)
            }
        }

        holder.decrementButton.setOnClickListener {
            val adapterPosition = holder.bindingAdapterPosition
            if (adapterPosition != RecyclerView.NO_POSITION) {
                habit.adjustTodayProgress(-habit.step)
                onHabitUpdated(habit)
                notifyItemChanged(adapterPosition)
            }
        }

        holder.itemView.setOnClickListener {
            onHabitClick(habit)
        }

        holder.itemView.setOnLongClickListener {
            onHabitLongClick(habit)
            true
        }
    }

    override fun getItemCount() = habits.size

    fun updateHabits(newHabits: List<Habit>) {
        habits.clear()
        habits.addAll(newHabits)
        notifyDataSetChanged()
    }
}

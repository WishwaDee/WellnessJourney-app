package com.wellness.tracker.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wellness.tracker.R
import com.wellness.tracker.models.Habit

class HabitsAdapter(
    private val habits: List<Habit>,
    private val onHabitAction: (Habit, String) -> Unit
) : RecyclerView.Adapter<HabitsAdapter.HabitViewHolder>() {

    class HabitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewName: TextView = itemView.findViewById(R.id.textViewHabitName)
        val textViewDescription: TextView = itemView.findViewById(R.id.textViewHabitDescription)
        val buttonToggle: Button = itemView.findViewById(R.id.buttonToggleHabit)
        val buttonEdit: Button = itemView.findViewById(R.id.buttonEditHabit)
        val buttonDelete: Button = itemView.findViewById(R.id.buttonDeleteHabit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_habit, parent, false)
        return HabitViewHolder(view)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        val habit = habits[position]
        
        holder.textViewName.text = habit.name
        holder.textViewDescription.text = habit.description
        
        holder.buttonToggle.setOnClickListener {
            onHabitAction(habit, "toggle")
        }
        
        holder.buttonEdit.setOnClickListener {
            onHabitAction(habit, "edit")
        }
        
        holder.buttonDelete.setOnClickListener {
            onHabitAction(habit, "delete")
        }
    }

    override fun getItemCount(): Int = habits.size
}
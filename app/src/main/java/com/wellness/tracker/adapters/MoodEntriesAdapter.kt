package com.wellness.tracker.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wellness.tracker.R
import com.wellness.tracker.models.MoodEntry

class MoodEntriesAdapter(
    private val moodEntries: List<MoodEntry>,
    private val onDeleteClick: (MoodEntry) -> Unit
) : RecyclerView.Adapter<MoodEntriesAdapter.MoodEntryViewHolder>() {

    class MoodEntryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewEmoji: TextView = itemView.findViewById(R.id.textViewMoodEmoji)
        val textViewMood: TextView = itemView.findViewById(R.id.textViewMoodName)
        val textViewDateTime: TextView = itemView.findViewById(R.id.textViewMoodDateTime)
        val textViewNote: TextView = itemView.findViewById(R.id.textViewMoodNote)
        val buttonDelete: Button = itemView.findViewById(R.id.buttonDeleteMood)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodEntryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mood_entry, parent, false)
        return MoodEntryViewHolder(view)
    }

    override fun onBindViewHolder(holder: MoodEntryViewHolder, position: Int) {
        val moodEntry = moodEntries[position]
        
        holder.textViewEmoji.text = moodEntry.emoji
        holder.textViewMood.text = moodEntry.mood
        holder.textViewDateTime.text = "${moodEntry.date} ${moodEntry.time}"
        
        if (moodEntry.note.isNotEmpty()) {
            holder.textViewNote.text = moodEntry.note
            holder.textViewNote.visibility = View.VISIBLE
        } else {
            holder.textViewNote.visibility = View.GONE
        }
        
        holder.buttonDelete.setOnClickListener {
            onDeleteClick(moodEntry)
        }
    }

    override fun getItemCount(): Int = moodEntries.size
}
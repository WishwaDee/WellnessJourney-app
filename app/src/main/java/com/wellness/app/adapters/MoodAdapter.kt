package com.wellness.app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wellness.app.R
import com.wellness.app.models.MoodEntry

class MoodAdapter(
    private val moods: MutableList<MoodEntry>,
    private val onMoodClick: (MoodEntry) -> Unit
) : RecyclerView.Adapter<MoodAdapter.MoodViewHolder>() {

    class MoodViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val emojiText: TextView = view.findViewById(R.id.moodEmoji)
        val moodName: TextView = view.findViewById(R.id.moodName)
        val moodDate: TextView = view.findViewById(R.id.moodDate)
        val moodTime: TextView = view.findViewById(R.id.moodTime)
        val moodNote: TextView = view.findViewById(R.id.moodNote)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mood, parent, false)
        return MoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: MoodViewHolder, position: Int) {
        val mood = moods[position]

        holder.emojiText.text = mood.emoji
        holder.moodName.text = mood.moodName
        holder.moodDate.text = mood.getFormattedDate()
        holder.moodTime.text = mood.getFormattedTime()
        holder.moodNote.text = mood.note
        holder.moodNote.visibility = if (mood.note.isNotEmpty()) View.VISIBLE else View.GONE

        holder.itemView.setOnClickListener {
            onMoodClick(mood)
        }
    }

    override fun getItemCount() = moods.size

    fun updateMoods(newMoods: List<MoodEntry>) {
        moods.clear()
        moods.addAll(newMoods)
        notifyDataSetChanged()
    }
}

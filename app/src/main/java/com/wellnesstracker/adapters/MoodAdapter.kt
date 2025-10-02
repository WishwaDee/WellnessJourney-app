package com.wellnesstracker.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wellnesstracker.databinding.ItemMoodBinding
import com.wellnesstracker.models.MoodEntry
import java.text.SimpleDateFormat
import java.util.*

class MoodAdapter(
    private var moods: List<MoodEntry>,
    private val onMoodDelete: (MoodEntry) -> Unit
) : RecyclerView.Adapter<MoodAdapter.MoodViewHolder>() {

    inner class MoodViewHolder(private val binding: ItemMoodBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(mood: MoodEntry) {
            binding.textEmoji.text = mood.emoji
            binding.textMoodName.text = mood.moodName
            binding.textNote.text = mood.note

            val sdf = SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault())
            binding.textTimestamp.text = sdf.format(Date(mood.timestamp))

            binding.root.setOnLongClickListener {
                onMoodDelete(mood)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodViewHolder {
        val binding = ItemMoodBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MoodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MoodViewHolder, position: Int) {
        holder.bind(moods[position])
    }

    override fun getItemCount() = moods.size

    fun updateMoods(newMoods: List<MoodEntry>) {
        moods = newMoods
        notifyDataSetChanged()
    }
}
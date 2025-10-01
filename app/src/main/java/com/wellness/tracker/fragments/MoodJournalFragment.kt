package com.wellness.tracker.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.wellness.tracker.R
import com.wellness.tracker.adapters.MoodEntriesAdapter
import com.wellness.tracker.data.PreferencesManager
import com.wellness.tracker.databinding.FragmentMoodJournalBinding
import com.wellness.tracker.models.MoodEntry
import java.text.SimpleDateFormat
import java.util.*

class MoodJournalFragment : Fragment() {

    private var _binding: FragmentMoodJournalBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var moodEntriesAdapter: MoodEntriesAdapter
    private val moodEntries = mutableListOf<MoodEntry>()
    
    private val moodOptions = listOf(
        "😊" to "Happy",
        "😢" to "Sad", 
        "😤" to "Angry",
        "😴" to "Tired",
        "😰" to "Anxious",
        "😌" to "Calm",
        "🤔" to "Thoughtful",
        "😍" to "Excited"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoodJournalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        preferencesManager = PreferencesManager(requireContext())
        setupRecyclerView()
        setupMoodSelector()
        loadData()
        setupChart()
        setupShareButton()
    }

    private fun setupRecyclerView() {
        moodEntriesAdapter = MoodEntriesAdapter(moodEntries) { moodEntry ->
            deleteMoodEntry(moodEntry)
        }
        
        binding.recyclerViewMoods.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = moodEntriesAdapter
        }
    }

    private fun setupMoodSelector() {
        val moodContainer = binding.linearLayoutMoodSelector
        
        moodOptions.forEach { (emoji, mood) ->
            val button = layoutInflater.inflate(R.layout.item_mood_button, moodContainer, false)
            button.findViewById<android.widget.TextView>(R.id.textViewEmoji).text = emoji
            button.findViewById<android.widget.TextView>(R.id.textViewMood).text = mood
            
            button.setOnClickListener {
                showAddMoodDialog(emoji, mood)
            }
            
            moodContainer.addView(button)
        }
    }

    private fun showAddMoodDialog(emoji: String, mood: String) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_mood, null)
        val noteEdit = dialogView.findViewById<android.widget.EditText>(R.id.editTextMoodNote)
        
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Add Mood Entry")
            .setMessage("$emoji $mood")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val note = noteEdit.text.toString().trim()
                val moodEntry = MoodEntry(emoji = emoji, mood = mood, note = note)
                
                moodEntries.add(0, moodEntry) // Add to beginning
                preferencesManager.saveMoodEntries(moodEntries)
                moodEntriesAdapter.notifyItemInserted(0)
                binding.recyclerViewMoods.scrollToPosition(0)
                updateChart()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteMoodEntry(moodEntry: MoodEntry) {
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Delete Mood Entry")
            .setMessage("Are you sure you want to delete this mood entry?")
            .setPositiveButton("Delete") { _, _ ->
                val index = moodEntries.indexOf(moodEntry)
                if (index != -1) {
                    moodEntries.removeAt(index)
                    preferencesManager.saveMoodEntries(moodEntries)
                    moodEntriesAdapter.notifyItemRemoved(index)
                    updateChart()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun loadData() {
        moodEntries.clear()
        moodEntries.addAll(preferencesManager.getMoodEntries().sortedByDescending { it.timestamp })
        moodEntriesAdapter.notifyDataSetChanged()
    }

    private fun setupChart() {
        binding.moodChart.apply {
            description.isEnabled = false
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(true)
            setPinchZoom(true)
        }
        updateChart()
    }

    private fun updateChart() {
        val entries = mutableListOf<Entry>()
        val last7Days = moodEntries.take(7).reversed()
        
        last7Days.forEachIndexed { index, moodEntry ->
            val moodValue = when (moodEntry.mood) {
                "Happy" -> 5f
                "Excited" -> 4f
                "Calm" -> 3f
                "Thoughtful" -> 2f
                "Tired" -> 1f
                "Sad" -> 0f
                "Anxious" -> -1f
                "Angry" -> -2f
                else -> 0f
            }
            entries.add(Entry(index.toFloat(), moodValue))
        }

        if (entries.isNotEmpty()) {
            val dataSet = LineDataSet(entries, "Mood Trend").apply {
                color = resources.getColor(R.color.colorPrimary, null)
                setCircleColor(resources.getColor(R.color.colorAccent, null))
                lineWidth = 2f
                circleRadius = 4f
                setDrawCircleHole(false)
                valueTextSize = 10f
            }

            val lineData = LineData(dataSet)
            binding.moodChart.data = lineData
            binding.moodChart.invalidate()
        }
    }

    private fun setupShareButton() {
        binding.buttonShareMood.setOnClickListener {
            val recentMoods = moodEntries.take(7)
            val summary = buildString {
                append("My Mood Summary for the past week:\n\n")
                recentMoods.forEach { mood ->
                    append("${mood.emoji} ${mood.mood} - ${mood.date} ${mood.time}\n")
                    if (mood.note.isNotEmpty()) {
                        append("Note: ${mood.note}\n")
                    }
                    append("\n")
                }
                append("Shared from Wellness Tracker")
            }
            
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, summary)
                putExtra(Intent.EXTRA_SUBJECT, "My Mood Journal")
            }
            
            startActivity(Intent.createChooser(shareIntent, "Share Mood Summary"))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
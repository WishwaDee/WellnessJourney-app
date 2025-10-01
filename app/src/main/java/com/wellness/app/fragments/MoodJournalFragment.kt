package com.wellness.app.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.GridLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.wellness.app.R
import com.wellness.app.adapters.MoodAdapter
import com.wellness.app.models.MoodEmojis
import com.wellness.app.models.MoodEntry
import com.wellness.app.utils.DataManager
import java.util.*

class MoodJournalFragment : Fragment() {

    private lateinit var dataManager: DataManager
    private lateinit var moodAdapter: MoodAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var fabAddMood: FloatingActionButton
    private val moods = mutableListOf<MoodEntry>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mood_journal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataManager = DataManager(requireContext())

        recyclerView = view.findViewById(R.id.moodRecyclerView)
        fabAddMood = view.findViewById(R.id.fabAddMood)

        setupRecyclerView()
        loadMoods()

        fabAddMood.setOnClickListener {
            showAddMoodDialog()
        }
    }

    private fun setupRecyclerView() {
        moodAdapter = MoodAdapter(
            moods,
            onMoodClick = { mood -> showMoodDetailsDialog(mood) }
        )

        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = moodAdapter
        }
    }

    private fun loadMoods() {
        moods.clear()
        moods.addAll(dataManager.getMoodEntries().sortedByDescending { it.timestamp })
        moodAdapter.notifyDataSetChanged()
    }

    private fun saveMoods() {
        dataManager.saveMoodEntries(moods)
    }

    private fun showAddMoodDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_mood, null)
        val emojiGrid = dialogView.findViewById<GridLayout>(R.id.emojiGrid)
        val noteInput = dialogView.findViewById<EditText>(R.id.moodNoteInput)

        var selectedEmoji = ""
        var selectedMoodName = ""

        MoodEmojis.moods.forEach { (emoji, name) ->
            val emojiCard = layoutInflater.inflate(R.layout.item_emoji, emojiGrid, false) as CardView
            val emojiText = emojiCard.findViewById<TextView>(R.id.emojiText)
            emojiText.text = emoji

            emojiCard.setOnClickListener {
                selectedEmoji = emoji
                selectedMoodName = name

                for (i in 0 until emojiGrid.childCount) {
                    (emojiGrid.getChildAt(i) as? CardView)?.setCardBackgroundColor(
                        resources.getColor(android.R.color.white, null)
                    )
                }
                emojiCard.setCardBackgroundColor(
                    resources.getColor(R.color.mood_selected, null)
                )
            }

            emojiGrid.addView(emojiCard)
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Log Your Mood")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                if (selectedEmoji.isNotEmpty()) {
                    val note = noteInput.text.toString().trim()
                    val moodEntry = MoodEntry(
                        id = UUID.randomUUID().toString(),
                        emoji = selectedEmoji,
                        moodName = selectedMoodName,
                        note = note
                    )
                    moods.add(0, moodEntry)
                    saveMoods()
                    moodAdapter.notifyItemInserted(0)
                    recyclerView.smoothScrollToPosition(0)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showMoodDetailsDialog(mood: MoodEntry) {
        val message = buildString {
            append("${mood.emoji} ${mood.moodName}\n\n")
            append("Date: ${mood.getFormattedDate()}\n")
            append("Time: ${mood.getFormattedTime()}\n")
            if (mood.note.isNotEmpty()) {
                append("\nNote: ${mood.note}")
            }
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Mood Entry")
            .setMessage(message)
            .setPositiveButton("Share") { _, _ ->
                shareMood(mood)
            }
            .setNegativeButton("Delete") { _, _ ->
                deleteMood(mood)
            }
            .setNeutralButton("Close", null)
            .show()
    }

    private fun shareMood(mood: MoodEntry) {
        val shareText = "My mood on ${mood.getFormattedDate()}: ${mood.emoji} ${mood.moodName}"
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        startActivity(Intent.createChooser(shareIntent, "Share mood via"))
    }

    private fun deleteMood(mood: MoodEntry) {
        val position = moods.indexOf(mood)
        moods.remove(mood)
        saveMoods()
        moodAdapter.notifyItemRemoved(position)
    }
}

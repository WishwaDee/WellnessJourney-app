package com.wellness.app.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.wellness.app.R
import com.wellness.app.adapters.MoodAdapter
import com.wellness.app.models.MoodEmojis
import com.wellness.app.models.MoodEntry
import com.wellness.app.utils.DataManager
import java.util.UUID

class MoodJournalFragment : Fragment() {

    private lateinit var dataManager: DataManager
    private lateinit var moodAdapter: MoodAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var moodChipGroup: ChipGroup
    private lateinit var moodNoteInput: TextInputEditText
    private lateinit var saveButton: MaterialButton
    private lateinit var cancelButton: MaterialButton
    private lateinit var emptyState: TextView
    private val moods = mutableListOf<MoodEntry>()

    private var selectedEmoji: String? = null
    private var selectedMoodName: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_mood_journal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataManager = DataManager(requireContext())

        recyclerView = view.findViewById(R.id.moodRecyclerView)
        moodChipGroup = view.findViewById(R.id.moodChipGroup)
        moodNoteInput = view.findViewById(R.id.moodNoteInput)
        saveButton = view.findViewById(R.id.moodSaveButton)
        cancelButton = view.findViewById(R.id.moodCancelButton)
        emptyState = view.findViewById(R.id.moodEmptyState)

        setupRecyclerView()
        populateMoodChips()
        loadMoods()

        saveButton.setOnClickListener { submitMood() }
        cancelButton.setOnClickListener { resetMoodForm() }
        updateSaveButtonState()
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

    private fun populateMoodChips() {
        moodChipGroup.removeAllViews()
        MoodEmojis.moods.forEach { (emoji, name) ->
            val chip = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_mood_chip, moodChipGroup, false) as Chip
            chip.text = "$emoji  $name"
            chip.tag = Pair(emoji, name)
            moodChipGroup.addView(chip)
        }

        moodChipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                val chip = group.findViewById<Chip>(checkedIds[0])
                val tag = chip.tag as? Pair<*, *>
                selectedEmoji = tag?.first as? String
                selectedMoodName = tag?.second as? String
            } else {
                selectedEmoji = null
                selectedMoodName = null
            }
            updateSaveButtonState()
        }
    }

    private fun updateSaveButtonState() {
        saveButton.isEnabled = !selectedEmoji.isNullOrEmpty()
    }

    private fun loadMoods() {
        moods.clear()
        moods.addAll(dataManager.getMoodEntries().sortedByDescending { it.timestamp })
        moodAdapter.notifyDataSetChanged()
        updateEmptyState()
    }

    private fun saveMoods() {
        dataManager.saveMoodEntries(moods)
        updateEmptyState()
    }

    private fun updateEmptyState() {
        emptyState.visibility = if (moods.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun submitMood() {
        val emoji = selectedEmoji ?: return
        val name = selectedMoodName ?: return
        val note = moodNoteInput.text?.toString()?.trim().orEmpty()

        val moodEntry = MoodEntry(
            id = UUID.randomUUID().toString(),
            emoji = emoji,
            moodName = name,
            note = note
        )
        moods.add(0, moodEntry)
        saveMoods()
        moodAdapter.notifyItemInserted(0)
        recyclerView.smoothScrollToPosition(0)
        resetMoodForm()
    }

    private fun resetMoodForm() {
        moodChipGroup.clearCheck()
        selectedEmoji = null
        selectedMoodName = null
        moodNoteInput.setText("")
        updateSaveButtonState()
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

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.mood_entry_title)
            .setMessage(message)
            .setPositiveButton(R.string.action_share) { _, _ ->
                shareMood(mood)
            }
            .setNegativeButton(R.string.action_delete) { _, _ ->
                deleteMood(mood)
            }
            .setNeutralButton(R.string.action_close, null)
            .show()
    }

    private fun shareMood(mood: MoodEntry) {
        val shareText = getString(
            R.string.mood_share_format,
            mood.getFormattedDate(),
            mood.emoji,
            mood.moodName
        )
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        startActivity(Intent.createChooser(shareIntent, getString(R.string.mood_share_chooser)))
    }

    private fun deleteMood(mood: MoodEntry) {
        val position = moods.indexOf(mood)
        if (position >= 0) {
            moods.removeAt(position)
            saveMoods()
            moodAdapter.notifyItemRemoved(position)
        }
    }
}

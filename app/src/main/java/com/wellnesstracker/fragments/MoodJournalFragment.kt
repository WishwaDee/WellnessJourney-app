package com.wellnesstracker.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.wellnesstracker.R
import com.wellnesstracker.adapters.MoodAdapter
import com.wellnesstracker.databinding.FragmentMoodJournalBinding
import com.wellnesstracker.models.MoodEntry
import com.wellnesstracker.utils.DataManager

class MoodJournalFragment : Fragment() {

    private var _binding: FragmentMoodJournalBinding? = null
    private val binding get() = _binding!!
    private lateinit var dataManager: DataManager
    private lateinit var moodAdapter: MoodAdapter
    private val chipMoodMap = mutableMapOf<Int, MoodPreset>()
    private val emojiToChipId = mutableMapOf<String, Int>()
    private var editingMood: MoodEntry? = null

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
        dataManager = DataManager(requireContext())

        setupMoodChips()
        setupRecyclerView()
        setupActions()
        refreshMoods()
    }

    override fun onResume() {
        super.onResume()
        refreshMoods()
    }

    private fun setupMoodChips() {
        val moods = listOf(
            MoodPreset("😊", getString(R.string.mood_happy)),
            MoodPreset("😌", getString(R.string.mood_calm)),
            MoodPreset("😢", getString(R.string.mood_sad)),
            MoodPreset("😤", getString(R.string.mood_angry)),
            MoodPreset("😴", getString(R.string.mood_tired)),
            MoodPreset("😬", getString(R.string.mood_anxious)),
            MoodPreset("😄", getString(R.string.mood_grateful)),
            MoodPreset("💪", getString(R.string.mood_confident))
        )

        binding.chipGroupMoods.removeAllViews()
        chipMoodMap.clear()
        emojiToChipId.clear()

        moods.forEach { preset ->
            val chip = LayoutInflater.from(requireContext())
                .inflate(R.layout.view_mood_chip, binding.chipGroupMoods, false) as Chip
            chip.id = View.generateViewId()
            chip.text = getString(R.string.format_mood_chip, preset.emoji, preset.label)
            chip.isCheckable = true
            chipMoodMap[chip.id] = preset
            emojiToChipId[preset.emoji] = chip.id
            binding.chipGroupMoods.addView(chip)
        }
    }

    private fun setupRecyclerView() {
        moodAdapter = MoodAdapter(
            moods = dataManager.getMoods(),
            onMoodSelected = { mood ->
                val chipId = emojiToChipId[mood.emoji]
                enterEditingMode(mood, chipId)
            },
            onMoodDelete = { mood ->
                dataManager.deleteMood(mood.id)
                refreshMoods()
                Toast.makeText(requireContext(), R.string.mood_deleted, Toast.LENGTH_SHORT).show()
            }
        )

        binding.recyclerRecentMoods.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = moodAdapter
        }
    }

    private fun setupActions() {
        binding.buttonSaveMood.setOnClickListener { saveMoodEntry() }

        binding.buttonClear.setOnClickListener {
            if (editingMood != null) {
                exitEditingMode()
            } else {
                binding.chipGroupMoods.clearCheck()
                binding.inputNote.setText("")
            }
        }

        binding.buttonAddMood.setOnClickListener {
            (binding.root as? ScrollView)?.post {
                (binding.root as? ScrollView)?.smoothScrollTo(0, binding.inputLayoutNote.top)
            }
            binding.chipGroupMoods.requestFocus()
            if (editingMood != null) {
                exitEditingMode()
            }
        }

        exitEditingMode()
    }

    private fun saveMoodEntry() {
        val selectedChipId = binding.chipGroupMoods.checkedChipId
        val preset = chipMoodMap[selectedChipId]

        if (preset == null) {
            Toast.makeText(requireContext(), R.string.error_select_mood, Toast.LENGTH_SHORT).show()
            return
        }

        val note = binding.inputNote.text?.toString()?.trim().orEmpty()
        val currentEditing = editingMood
        if (currentEditing != null) {
            val updatedMood = currentEditing.copy(
                emoji = preset.emoji,
                moodName = preset.label,
                note = note.ifEmpty { null },
                timestamp = System.currentTimeMillis()
            )
            dataManager.updateMood(updatedMood)
            Toast.makeText(requireContext(), R.string.mood_updated, Toast.LENGTH_SHORT).show()
            exitEditingMode()
        } else {
            val mood = MoodEntry(
                emoji = preset.emoji,
                moodName = preset.label,
                note = note.ifEmpty { null },
                date = dataManager.getTodayDate()
            )

            dataManager.addMood(mood)
            Toast.makeText(requireContext(), R.string.mood_saved, Toast.LENGTH_SHORT).show()

            binding.chipGroupMoods.clearCheck()
            binding.inputNote.setText("")
        }

        refreshMoods()
    }

    private fun refreshMoods() {
        val moods = dataManager.getMoods()
        moodAdapter.updateMoods(moods)
        updateEmptyState(moods.isNotEmpty())
    }

    private fun updateEmptyState(hasMoods: Boolean) {
        binding.recyclerRecentMoods.isVisible = hasMoods
        binding.textEmptyState.isVisible = !hasMoods
        binding.textEmptySubtitle.isVisible = !hasMoods
    }

    private fun enterEditingMode(mood: MoodEntry, chipId: Int?) {
        editingMood = mood
        binding.textEditingState.isVisible = true
        binding.textEditingState.text = getString(R.string.editing_mood_hint, mood.emoji, mood.moodName)
        binding.buttonSaveMood.text = getString(R.string.update_mood)
        binding.buttonClear.text = getString(R.string.cancel)
        binding.inputNote.setText(mood.note.orEmpty())
        if (chipId != null) {
            binding.chipGroupMoods.check(chipId)
        } else {
            binding.chipGroupMoods.clearCheck()
        }
    }

    private fun exitEditingMode() {
        editingMood = null
        binding.textEditingState.isVisible = false
        binding.buttonSaveMood.text = getString(R.string.save_mood)
        binding.buttonClear.text = getString(R.string.clear_mood)
        binding.inputNote.setText("")
        binding.chipGroupMoods.clearCheck()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private data class MoodPreset(val emoji: String, val label: String)
}

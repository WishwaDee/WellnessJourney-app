package com.yourcompany.wellnesstracker.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.wellnesstracker.R
import com.wellnesstracker.databinding.DialogAddMoodBinding

class AddMoodDialog(
    private val onSave: (emoji: String, moodName: String, note: String) -> Unit
) : DialogFragment() {

    private var _binding: DialogAddMoodBinding? = null
    private val binding get() = _binding!!

    private var selectedEmoji = "😊"
    private var selectedMood = "Happy"

    private val moodButtons = mutableListOf<Button>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogAddMoodBinding.inflate(LayoutInflater.from(context))

        setupMoodButtons()

        return AlertDialog.Builder(requireContext())
            .setTitle("How are you feeling?")
            .setView(binding.root)
            .setPositiveButton("Save") { _, _ ->
                val note = binding.editTextNote.text.toString().trim()
                onSave(selectedEmoji, selectedMood, note)
            }
            .setNegativeButton("Cancel", null)
            .create()
    }

    private fun setupMoodButtons() {
        moodButtons.addAll(listOf(
            binding.buttonGreat,
            binding.buttonHappy,
            binding.buttonOkay,
            binding.buttonSad,
            binding.buttonAngry
        ))

        binding.buttonGreat.setOnClickListener { selectMood("😄", "Great", it as Button) }
        binding.buttonHappy.setOnClickListener { selectMood("😊", "Happy", it as Button) }
        binding.buttonOkay.setOnClickListener { selectMood("😐", "Okay", it as Button) }
        binding.buttonSad.setOnClickListener { selectMood("😢", "Sad", it as Button) }
        binding.buttonAngry.setOnClickListener { selectMood("😠", "Angry", it as Button) }

        // Select Happy by default
        selectMood("😊", "Happy", binding.buttonHappy)
    }

    private fun selectMood(emoji: String, mood: String, button: Button) {
        selectedEmoji = emoji
        selectedMood = mood

        moodButtons.forEach { btn ->
            btn.setBackgroundColor(
                ContextCompat.getColor(requireContext(), R.color.mood_button_default)
            )
        }

        button.setBackgroundColor(
            ContextCompat.getColor(requireContext(), R.color.purple_500)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
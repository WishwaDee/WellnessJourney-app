package com.wellnesstracker.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.content.res.ColorStateList
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.google.android.material.button.MaterialButton
import com.wellnesstracker.R
import com.wellnesstracker.databinding.DialogAddMoodBinding

class AddMoodDialog(
    private val onSave: (emoji: String, moodName: String, note: String) -> Unit
) : DialogFragment() {

    private var _binding: DialogAddMoodBinding? = null
    private val binding get() = _binding!!

    private var selectedEmoji = "😊"
    private var selectedMood = "Happy"

    private val moodButtons = mutableListOf<MaterialButton>()

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
        moodButtons.addAll(
            listOf(
                binding.buttonGreat,
                binding.buttonHappy,
                binding.buttonOkay,
                binding.buttonSad,
                binding.buttonAngry
            )
        )

        binding.buttonGreat.setOnClickListener { selectMood("😄", "Great", binding.buttonGreat) }
        binding.buttonHappy.setOnClickListener { selectMood("😊", "Happy", binding.buttonHappy) }
        binding.buttonOkay.setOnClickListener { selectMood("😐", "Okay", binding.buttonOkay) }
        binding.buttonSad.setOnClickListener { selectMood("😢", "Sad", binding.buttonSad) }
        binding.buttonAngry.setOnClickListener { selectMood("😠", "Angry", binding.buttonAngry) }

        // Select Happy by default
        selectMood("😊", "Happy", binding.buttonHappy)
    }

    private fun selectMood(emoji: String, mood: String, button: MaterialButton) {
        selectedEmoji = emoji
        selectedMood = mood

        moodButtons.forEach { btn ->
            val baseColor = ContextCompat.getColor(requireContext(), R.color.mood_button_default)
            val secondaryColor = ContextCompat.getColor(requireContext(), R.color.color_secondary)
            btn.backgroundTintList = ColorStateList.valueOf(baseColor)
            btn.setStrokeColor(ColorStateList.valueOf(secondaryColor))
            btn.setTextColor(secondaryColor)
        }

        val accent = ContextCompat.getColor(requireContext(), R.color.color_accent)
        button.backgroundTintList = ColorStateList.valueOf(accent)
        button.setStrokeColor(ColorStateList.valueOf(accent))
        button.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
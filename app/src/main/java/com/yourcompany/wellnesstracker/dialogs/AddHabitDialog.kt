package com.yourcompany.wellnesstracker.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.wellnesstracker.R
import com.wellnesstracker.databinding.DialogAddHabitBinding
import com.wellnesstracker.models.Habit

class AddHabitDialog(
    private val existingHabit: Habit? = null,
    private val onSave: (name: String, description: String, icon: String) -> Unit
) : DialogFragment() {

    private var _binding: DialogAddHabitBinding? = null
    private val binding get() = _binding!!

    private val icons = listOf(
        "💧", "🧘", "🏃", "📚", "🥗", "😴", "🎯",
        "💪", "🚶", "🧠", "❤️", "☕", "🎵", "✍️"
    )

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogAddHabitBinding.inflate(LayoutInflater.from(context))

        setupIconSpinner()

        existingHabit?.let { habit ->
            binding.editTextName.setText(habit.name)
            binding.editTextDescription.setText(habit.description)
            val iconPosition = icons.indexOf(habit.icon)
            if (iconPosition >= 0) {
                binding.spinnerIcon.setSelection(iconPosition)
            }
        }

        return AlertDialog.Builder(requireContext())
            .setTitle(if (existingHabit == null) "Add Habit" else "Edit Habit")
            .setView(binding.root)
            .setPositiveButton("Save") { _, _ ->
                val name = binding.editTextName.text.toString().trim()
                val description = binding.editTextDescription.text.toString().trim()
                val icon = binding.spinnerIcon.selectedItem.toString()

                if (name.isNotEmpty()) {
                    onSave(name, description, icon)
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
    }

    private fun setupIconSpinner() {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, icons)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerIcon.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
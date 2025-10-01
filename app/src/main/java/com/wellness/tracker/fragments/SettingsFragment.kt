package com.wellness.tracker.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.wellness.tracker.R
import com.wellness.tracker.data.PreferencesManager
import com.wellness.tracker.databinding.FragmentSettingsBinding
import com.wellness.tracker.notifications.HydrationReminderService

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var preferencesManager: PreferencesManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        preferencesManager = PreferencesManager(requireContext())
        setupUserSettings()
        setupHydrationSettings()
        loadSettings()
    }

    private fun setupUserSettings() {
        binding.editTextUserName.setText(preferencesManager.getUserName())
        
        binding.buttonSaveName.setOnClickListener {
            val name = binding.editTextUserName.text.toString().trim()
            if (name.isNotEmpty()) {
                preferencesManager.setUserName(name)
                android.widget.Toast.makeText(context, "Name saved!", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupHydrationSettings() {
        // Setup interval spinner
        val intervals = arrayOf("15 minutes", "30 minutes", "1 hour", "2 hours", "3 hours")
        val intervalValues = arrayOf(15, 30, 60, 120, 180)
        
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, intervals)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerHydrationInterval.adapter = adapter
        
        binding.spinnerHydrationInterval.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                preferencesManager.setHydrationInterval(intervalValues[position])
                if (binding.switchHydrationReminder.isChecked) {
                    HydrationReminderService.scheduleReminders(requireContext())
                }
            }
            
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        
        // Setup reminder switch
        binding.switchHydrationReminder.setOnCheckedChangeListener { _, isChecked ->
            preferencesManager.setHydrationReminderEnabled(isChecked)
            
            if (isChecked) {
                HydrationReminderService.scheduleReminders(requireContext())
                android.widget.Toast.makeText(context, "Hydration reminders enabled", android.widget.Toast.LENGTH_SHORT).show()
            } else {
                HydrationReminderService.cancelReminders(requireContext())
                android.widget.Toast.makeText(context, "Hydration reminders disabled", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadSettings() {
        // Load user name
        binding.editTextUserName.setText(preferencesManager.getUserName())
        
        // Load hydration settings
        binding.switchHydrationReminder.isChecked = preferencesManager.isHydrationReminderEnabled()
        
        val interval = preferencesManager.getHydrationInterval()
        val intervalIndex = when (interval) {
            15 -> 0
            30 -> 1
            60 -> 2
            120 -> 3
            180 -> 4
            else -> 2 // Default to 1 hour
        }
        binding.spinnerHydrationInterval.setSelection(intervalIndex)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
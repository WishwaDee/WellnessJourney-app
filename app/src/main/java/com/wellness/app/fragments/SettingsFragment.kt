package com.wellness.app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.wellness.app.R
import com.wellness.app.utils.DataManager
import com.wellness.app.utils.NotificationHelper

class SettingsFragment : Fragment() {

    private lateinit var dataManager: DataManager
    private lateinit var notificationHelper: NotificationHelper

    private lateinit var hydrationSwitch: Switch
    private lateinit var intervalInput: EditText
    private lateinit var saveButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataManager = DataManager(requireContext())
        notificationHelper = NotificationHelper(requireContext())

        hydrationSwitch = view.findViewById(R.id.hydrationSwitch)
        intervalInput = view.findViewById(R.id.intervalInput)
        saveButton = view.findViewById(R.id.saveSettingsButton)

        loadSettings()

        saveButton.setOnClickListener {
            saveSettings()
        }
    }

    private fun loadSettings() {
        hydrationSwitch.isChecked = dataManager.isHydrationEnabled()
        intervalInput.setText(dataManager.getHydrationInterval().toString())
    }

    private fun saveSettings() {
        val enabled = hydrationSwitch.isChecked
        val interval = intervalInput.text.toString().toIntOrNull() ?: 60

        if (interval < 15) {
            Toast.makeText(requireContext(), "Minimum interval is 15 minutes", Toast.LENGTH_SHORT).show()
            return
        }

        dataManager.setHydrationEnabled(enabled)
        dataManager.setHydrationInterval(interval)

        if (enabled) {
            notificationHelper.scheduleHydrationReminder(interval)
            Toast.makeText(requireContext(), "Hydration reminders enabled", Toast.LENGTH_SHORT).show()
        } else {
            notificationHelper.cancelHydrationReminder()
            Toast.makeText(requireContext(), "Hydration reminders disabled", Toast.LENGTH_SHORT).show()
        }
    }
}

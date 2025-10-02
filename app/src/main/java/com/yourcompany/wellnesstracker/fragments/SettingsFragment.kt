package com.yourcompany.wellnesstracker.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.wellnesstracker.databinding.FragmentSettingsBinding
import com.wellnesstracker.utils.DataManager
import com.wellnesstracker.utils.NotificationHelper

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var dataManager: DataManager
    private lateinit var notificationHelper: NotificationHelper

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            enableNotifications()
        } else {
            Toast.makeText(
                requireContext(),
                "Notification permission denied",
                Toast.LENGTH_SHORT
            ).show()
            binding.switchNotifications.isChecked = false
        }
    }

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

        dataManager = DataManager(requireContext())
        notificationHelper = NotificationHelper(requireContext())

        setupIntervalSpinner()
        setupNotificationSwitch()
        setupButtons()
        loadSettings()
    }

    private fun setupIntervalSpinner() {
        val intervals = arrayOf("30 minutes", "1 hour", "2 hours", "3 hours", "4 hours")
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            intervals
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerInterval.adapter = adapter
    }

    private fun setupNotificationSwitch() {
        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkAndRequestNotificationPermission()
            } else {
                disableNotifications()
            }
        }
    }

    private fun setupButtons() {
        binding.buttonSaveSettings.setOnClickListener {
            saveSettings()
        }

        binding.buttonClearData.setOnClickListener {
            showClearDataDialog()
        }
    }

    private fun checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    enableNotifications()
                }
                else -> {
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            enableNotifications()
        }
    }

    private fun enableNotifications() {
        dataManager.setNotificationsEnabled(true)
        val interval = getSelectedInterval()
        dataManager.setWaterInterval(interval)
        notificationHelper.scheduleWaterReminder(interval)
        Toast.makeText(requireContext(), "Notifications enabled", Toast.LENGTH_SHORT).show()
    }

    private fun disableNotifications() {
        dataManager.setNotificationsEnabled(false)
        notificationHelper.cancelWaterReminder()
        Toast.makeText(requireContext(), "Notifications disabled", Toast.LENGTH_SHORT).show()
    }

    private fun getSelectedInterval(): Int {
        return when (binding.spinnerInterval.selectedItemPosition) {
            0 -> 30
            1 -> 60
            2 -> 120
            3 -> 180
            4 -> 240
            else -> 60
        }
    }

    private fun getIntervalPosition(minutes: Int): Int {
        return when (minutes) {
            30 -> 0
            60 -> 1
            120 -> 2
            180 -> 3
            240 -> 4
            else -> 1
        }
    }

    private fun saveSettings() {
        val interval = getSelectedInterval()
        dataManager.setWaterInterval(interval)

        if (binding.switchNotifications.isChecked) {
            notificationHelper.scheduleWaterReminder(interval)
        }

        Toast.makeText(requireContext(), "Settings saved", Toast.LENGTH_SHORT).show()
    }

    private fun loadSettings() {
        val interval = dataManager.getWaterInterval()
        binding.spinnerInterval.setSelection(getIntervalPosition(interval))
        binding.switchNotifications.isChecked = dataManager.areNotificationsEnabled()
    }

    private fun showClearDataDialog() {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Clear All Data")
            .setMessage("This will delete all habits, mood entries, and settings. This action cannot be undone.")
            .setPositiveButton("Clear") { _, _ ->
                clearAllData()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun clearAllData() {
        dataManager.saveHabits(emptyList())
        dataManager.saveCompletions(emptyList())
        dataManager.saveMoods(emptyList())
        Toast.makeText(requireContext(), "All data cleared", Toast.LENGTH_SHORT).show()

        // Refresh the app
        requireActivity().recreate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
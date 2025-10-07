package com.wellnesstracker.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.wellnesstracker.R
import com.wellnesstracker.databinding.FragmentHydrationBinding
import com.wellnesstracker.utils.DataManager
import com.wellnesstracker.utils.NotificationHelper
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HydrationFragment : Fragment() {

    private var _binding: FragmentHydrationBinding? = null
    private val binding get() = _binding!!
    private lateinit var dataManager: DataManager
    private lateinit var notificationHelper: NotificationHelper
    private var customAmount = 250

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHydrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataManager = DataManager(requireContext())
        notificationHelper = NotificationHelper(requireContext())

        setupQuickButtons()
        setupCustomControls()
        setupReminderControls()
        updateHydrationSummary()
    }

    override fun onResume() {
        super.onResume()
        updateHydrationSummary()
        updateReminderStatus()
    }

    private fun setupQuickButtons() {
        val quickButtons: Map<MaterialButton, Int> = mapOf(
            binding.buttonQuick250 to 250,
            binding.buttonQuick500 to 500,
            binding.buttonQuick750 to 750,
            binding.buttonQuick1000 to 1000,
            binding.buttonQuickAdd to 250
        )

        binding.buttonQuickAdd.text = getString(R.string.format_add_amount, 250)

        quickButtons.forEach { (button, amount) ->
            button.setOnClickListener { addHydration(amount) }
        }
    }

    private fun setupCustomControls() {
        updateCustomValue()

        binding.buttonDecrease.setOnClickListener {
            customAmount = (customAmount - 50).coerceAtLeast(50)
            updateCustomValue()
        }

        binding.buttonIncrease.setOnClickListener {
            customAmount += 50
            updateCustomValue()
        }

        binding.buttonAddCustom.setOnClickListener {
            addHydration(customAmount)
        }
    }

    private fun addHydration(amount: Int) {
        dataManager.addHydrationEntry(amount)
        updateHydrationSummary()
    }

    private fun setupReminderControls() {
        updateReminderStatus()

        binding.buttonSetReminder.setOnClickListener {
            showReminderDialog()
        }

        binding.buttonCancelReminder.setOnClickListener {
            notificationHelper.cancelOneTimeWaterReminder()
            updateReminderStatus()
            Toast.makeText(requireContext(), R.string.hydration_reminder_cancelled, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showReminderDialog() {
        val options = listOf(
            10L to getString(R.string.reminder_option_seconds, 10),
            30L to getString(R.string.reminder_option_seconds, 30),
            60L to getString(R.string.reminder_option_minutes, 1),
            5 * 60L to getString(R.string.reminder_option_minutes, 5),
            15 * 60L to getString(R.string.reminder_option_minutes, 15),
            30 * 60L to getString(R.string.reminder_option_minutes, 30)
        )

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.hydration_reminder_dialog_title)
            .setItems(options.map { it.second }.toTypedArray()) { _, which ->
                val (delaySeconds, label) = options[which]
                notificationHelper.scheduleOneTimeWaterReminder(delaySeconds)
                updateReminderStatus()
                Toast.makeText(
                    requireContext(),
                    getString(R.string.hydration_reminder_scheduled_for, label),
                    Toast.LENGTH_SHORT
                ).show()

                if (!dataManager.areNotificationsEnabled()) {
                    Toast.makeText(
                        requireContext(),
                        R.string.hydration_reminder_permission_warning,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun updateReminderStatus() {
        val nextReminder = dataManager.getNextHydrationReminderTime()
        val isScheduled = nextReminder > System.currentTimeMillis()

        if (isScheduled) {
            val timeFormatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val formattedTime = timeFormatter.format(Date(nextReminder))
            binding.textReminderStatus.text =
                getString(R.string.hydration_reminder_status_at, formattedTime)
        } else {
            binding.textReminderStatus.text =
                getString(R.string.hydration_reminder_status_off)
            if (nextReminder != 0L) {
                dataManager.clearNextHydrationReminderTime()
            }
        }

        binding.buttonCancelReminder.isVisible = isScheduled
    }

    private fun updateHydrationSummary() {
        val goal = dataManager.getHydrationGoal()
        val total = dataManager.getTodayHydrationTotal()
        val remaining = (goal - total).coerceAtLeast(0)
        val percentage = dataManager.getHydrationProgressPercentage()

        binding.textDailyGoal.text = getString(R.string.format_daily_goal, goal)
        binding.textProgressValue.text = getString(R.string.format_progress_value, total, goal)
        binding.progressHydration.progress = percentage
        binding.textRemaining.text = getString(R.string.format_hydration_remaining, remaining)

        updateHistory()
    }

    private fun updateHistory() {
        val entries = dataManager.getHydrationEntries().filter {
            it.date == dataManager.getTodayDate()
        }

        binding.textNoHistory.isVisible = entries.isEmpty()
        binding.textHistorySubtitle.isVisible = entries.isEmpty()
        binding.containerHistory.removeAllViews()

        if (entries.isEmpty()) {
            return
        }

        val timeFormatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val numberFormatter = NumberFormat.getIntegerInstance()

        entries.take(5).forEach { entry ->
            val textView = TextView(requireContext()).apply {
                text = getString(
                    R.string.format_history_entry,
                    numberFormatter.format(entry.amountMl),
                    timeFormatter.format(Date(entry.timestamp))
                )
                setTextColor(resources.getColor(android.R.color.darker_gray, null))
                textSize = 14f
                setPadding(0, 8, 0, 8)
            }
            binding.containerHistory.addView(textView)
        }
    }

    private fun updateCustomValue() {
        binding.textCustomValue.text = customAmount.toString()
        binding.buttonAddCustom.text = getString(R.string.format_add_amount, customAmount)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


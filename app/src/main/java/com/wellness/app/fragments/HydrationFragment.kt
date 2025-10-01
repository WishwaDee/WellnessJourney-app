package com.wellness.app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import com.wellness.app.R
import com.wellness.app.utils.DataManager
import java.text.SimpleDateFormat
import java.util.Locale

class HydrationFragment : Fragment() {

    private lateinit var dataManager: DataManager
    private lateinit var progressIndicator: LinearProgressIndicator
    private lateinit var progressLabel: TextView
    private lateinit var remainingLabel: TextView
    private lateinit var quickAddButtons: List<MaterialButton>
    private lateinit var customAmountInput: TextInputEditText
    private lateinit var addCustomButton: MaterialButton
    private lateinit var historyEmptyText: TextView
    private lateinit var historyContainer: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_hydration, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataManager = DataManager(requireContext())

        progressIndicator = view.findViewById(R.id.hydrationProgress)
        progressLabel = view.findViewById(R.id.hydrationProgressLabel)
        remainingLabel = view.findViewById(R.id.hydrationRemainingLabel)
        customAmountInput = view.findViewById(R.id.hydrationCustomInput)
        addCustomButton = view.findViewById(R.id.hydrationAddCustomButton)
        historyEmptyText = view.findViewById(R.id.hydrationHistoryEmpty)
        historyContainer = view.findViewById(R.id.hydrationHistoryContainer)

        quickAddButtons = listOf(
            view.findViewById(R.id.quickAdd250),
            view.findViewById(R.id.quickAdd500),
            view.findViewById(R.id.quickAdd750),
            view.findViewById(R.id.quickAdd1000)
        )

        quickAddButtons.forEach { button ->
            button.setOnClickListener {
                val amount = button.tag?.toString()?.toIntOrNull() ?: 0
                if (amount > 0) {
                    dataManager.addHydration(amount)
                    refreshHydration()
                }
            }
        }

        addCustomButton.setOnClickListener {
            val amount = customAmountInput.text?.toString()?.toIntOrNull() ?: 0
            if (amount > 0) {
                dataManager.addHydration(amount)
                customAmountInput.setText("")
                refreshHydration()
            }
        }

        refreshHydration()
    }

    private fun refreshHydration() {
        val goal = dataManager.getHydrationGoal()
        val total = dataManager.getTodayHydration()
        val percent = dataManager.getHydrationCompletionPercentage()
        val remaining = dataManager.getHydrationRemaining()

        progressIndicator.progress = percent
        progressLabel.text = getString(R.string.hydration_progress_format, total, goal)
        remainingLabel.text = if (remaining > 0) {
            getString(R.string.hydration_remaining_format, remaining)
        } else {
            getString(R.string.hydration_goal_met)
        }

        val history = dataManager.getHydrationHistory().toList()
            .sortedByDescending { it.first }
            .take(5)

        historyContainer.removeAllViews()
        if (history.isEmpty()) {
            historyEmptyText.visibility = View.VISIBLE
        } else {
            historyEmptyText.visibility = View.GONE
            history.forEach { (date, amount) ->
                val entryView = layoutInflater.inflate(R.layout.item_hydration_history, historyContainer, false)
                entryView.findViewById<TextView>(R.id.historyDate).text = formatHistoryDate(date)
                entryView.findViewById<TextView>(R.id.historyAmount).text =
                    getString(R.string.hydration_history_amount, amount)
                historyContainer.addView(entryView)
            }
        }
    }

    private fun formatHistoryDate(raw: String): String {
        return runCatching {
            val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = parser.parse(raw) ?: return raw
            val formatter = SimpleDateFormat("MMM d", Locale.getDefault())
            formatter.format(date)
        }.getOrDefault(raw)
    }
}

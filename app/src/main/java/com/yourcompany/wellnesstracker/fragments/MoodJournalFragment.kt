package com.yourcompany.wellnesstracker.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.wellnesstracker.R
import com.yourcompany.wellnesstracker.adapters.MoodAdapter
import com.wellnesstracker.databinding.FragmentMoodJournalBinding
import com.yourcompany.wellnesstracker.dialogs.AddMoodDialog
import com.wellnesstracker.models.MoodEntry
import com.wellnesstracker.utils.DataManager
import java.text.SimpleDateFormat
import java.util.*

class MoodJournalFragment : Fragment() {
    private var _binding: FragmentMoodJournalBinding? = null
    private val binding get() = _binding!!
    private lateinit var dataManager: DataManager
    private lateinit var moodAdapter: MoodAdapter

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
        setupRecyclerView()
        setupFab()
        setupChart()
        setupShareButton()
    }

    private fun setupRecyclerView() {
        moodAdapter = MoodAdapter(
            moods = dataManager.getMoods(),
            onMoodDelete = { mood -> deleteMood(mood) }
        )

        binding.recyclerViewMoods.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = moodAdapter
        }

        updateEmptyState()
    }

    private fun setupFab() {
        binding.fabAddMood.setOnClickListener {
            showAddMoodDialog()
        }
    }

    private fun showAddMoodDialog() {
        AddMoodDialog { emoji, moodName, note ->
            val mood = MoodEntry(
                emoji = emoji,
                moodName = moodName,
                note = note,
                date = dataManager.getTodayDate()
            )
            dataManager.addMood(mood)
            moodAdapter.updateMoods(dataManager.getMoods())
            updateEmptyState()
            setupChart()
        }.show(childFragmentManager, "AddMoodDialog")
    }

    private fun deleteMood(mood: MoodEntry) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Mood Entry")
            .setMessage("Are you sure you want to delete this mood entry?")
            .setPositiveButton("Delete") { _, _ ->
                dataManager.deleteMood(mood.id)
                moodAdapter.updateMoods(dataManager.getMoods())
                updateEmptyState()
                setupChart()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun setupChart() {
        val moods = dataManager.getMoods().takeLast(7)
        if (moods.isEmpty()) {
            binding.chartMood.visibility = View.GONE
            return
        }

        binding.chartMood.visibility = View.VISIBLE

        // Map mood names to scores
        val moodScores = mapOf(
            "Great" to 5f,
            "Happy" to 4f,
            "Okay" to 3f,
            "Sad" to 2f,
            "Angry" to 1f,
            "Stressed" to 2f,
            "Calm" to 4f
        )

        val entries = moods.mapIndexed { index, mood ->
            Entry(index.toFloat(), moodScores[mood.moodName] ?: 3f)
        }

        val dataSet = LineDataSet(entries, "Mood Trend").apply {
            color = resources.getColor(R.color.purple_500, null)
            valueTextColor = resources.getColor(R.color.black, null)
            lineWidth = 2f
            circleRadius = 4f
            setCircleColor(resources.getColor(R.color.purple_500, null))
            setDrawFilled(true)
            fillColor = resources.getColor(R.color.purple_200, null)
        }

        binding.chartMood.apply {
            data = LineData(dataSet)
            description.isEnabled = false
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.valueFormatter = object : ValueFormatter() {
                private val sdf = SimpleDateFormat("MM/dd", Locale.getDefault())
                override fun getFormattedValue(value: Float): String {
                    return if (value.toInt() < moods.size) {
                        sdf.format(Date(moods[value.toInt()].timestamp))
                    } else ""
                }
            }
            axisLeft.axisMinimum = 0f
            axisLeft.axisMaximum = 6f
            axisRight.isEnabled = false
            invalidate()
        }
    }

    private fun setupShareButton() {
        binding.buttonShareMood.setOnClickListener {
            shareMoodSummary()
        }
    }

    private fun shareMoodSummary() {
        val moods = dataManager.getMoods().take(7)
        if (moods.isEmpty()) {
            return
        }

        val summary = buildString {
            appendLine("📊 My Mood Summary")
            appendLine("==================")
            moods.forEach { mood ->
                val sdf = SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault())
                appendLine("${mood.emoji} ${mood.moodName} - ${sdf.format(Date(mood.timestamp))}")
                if (mood.note.isNotEmpty()) {
                    appendLine("   Note: ${mood.note}")
                }
            }
        }

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, summary)
            putExtra(Intent.EXTRA_SUBJECT, "My Mood Journal")
        }
        startActivity(Intent.createChooser(shareIntent, "Share Mood Summary"))
    }

    private fun updateEmptyState() {
        if (dataManager.getMoods().isEmpty()) {
            binding.recyclerViewMoods.visibility = View.GONE
            binding.emptyStateLayout.visibility = View.VISIBLE
        } else {
            binding.recyclerViewMoods.visibility = View.VISIBLE
            binding.emptyStateLayout.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
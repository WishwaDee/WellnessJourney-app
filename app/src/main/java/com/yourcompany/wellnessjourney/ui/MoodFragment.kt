package com.yourcompany.wellnessjourney.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.yourcompany.wellnessjourney.R

class MoodFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_mood, container, false)
        view.findViewById<TextView>(R.id.text_mood).text = "Mood Journal Screen"
        return view
    }
}
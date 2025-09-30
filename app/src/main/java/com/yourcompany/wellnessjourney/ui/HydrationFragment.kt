package com.yourcompany.wellnessjourney.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.yourcompany.wellnessjourney.R

class HydrationFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_hydration, container, false)
        view.findViewById<TextView>(R.id.text_hydration).text = "Hydration Tracker Screen"
        return view
    }
}
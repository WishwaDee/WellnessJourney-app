package com.yourcompany.wellnessjourney // <<< YOUR CORRECT PACKAGE NAME

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.yourcompany.wellnessjourney.R // <<< YOUR CORRECT R FILE IMPORT

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // This sets the layout defined above

        val navView: BottomNavigationView = findViewById(R.id.bottom_nav_view)

        // Find the NavController associated with your NavHostFragment
        val navController = findNavController(R.id.nav_host_fragment) // <<< Looks for NavHostFragment by ID

        // Link the BottomNavigationView to the NavController
        navView.setupWithNavController(navController)
    }
}
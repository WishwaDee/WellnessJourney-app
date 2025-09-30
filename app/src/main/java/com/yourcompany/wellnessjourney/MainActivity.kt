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
        setContentView(R.layout.activity_main)

        val navView: BottomNavigationView = findViewById(R.id.bottom_nav_view)

        // The error indicates this line: MainActivity.kt:20
        // It means 'nav_host_fragment' is not yet inflated or configured properly
        val navController = findNavController(R.id.nav_host_fragment)

        navView.setupWithNavController(navController)
    }
}
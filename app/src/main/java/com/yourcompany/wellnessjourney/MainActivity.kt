package com.yourcompany.wellnessjourney

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.yourcompany.wellnessjourney.R

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController // Declare navController here

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as? NavHostFragment
            ?: throw IllegalStateException("NavHostFragment not found")
        navController = navHostFragment.navController // Initialize it

        val navView: BottomNavigationView = findViewById(R.id.bottom_nav_view)
        navView.setupWithNavController(navController)
    }

    // Public method to access NavController from fragments
    fun getNavController(): NavController {
        return navController
    }
}
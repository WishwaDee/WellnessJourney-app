package com.yourcompany.wellnessjourney

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity

import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.yourcompany.wellnessjourney.R
import com.yourcompany.wellnessjourney.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = (supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as? NavHostFragment)
            ?.navController


        if (navController == null) {
            Log.e(TAG, "NavHostFragment not found; bottom navigation disabled.")
            binding.bottomNavView.visibility = View.GONE
            binding.bottomNavView.isEnabled = false
            return
        }

        binding.bottomNavView.setupWithNavController(navController)
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}


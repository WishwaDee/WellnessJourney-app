package com.wellnesstracker

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.wellnesstracker.databinding.ActivitySignupBinding
import com.wellnesstracker.models.User
import com.wellnesstracker.utils.SessionManager

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        binding.buttonSignup.setOnClickListener {
            val name = binding.inputName.text?.toString()?.trim().orEmpty()
            val email = binding.inputEmail.text?.toString()?.trim().orEmpty()
            val password = binding.inputPassword.text?.toString()?.trim().orEmpty()

            when {
                name.isEmpty() -> showToast(getString(R.string.error_name_required))
                email.isEmpty() -> showToast(getString(R.string.error_email_required))
                password.length < 6 -> showToast(getString(R.string.error_password_length))
                else -> {
                    val user = User(name = name, email = email, password = password)
                    sessionManager.saveUser(user)
                    sessionManager.setLoggedIn(true)
                    navigateToMain()
                }
            }
        }

        binding.textLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}
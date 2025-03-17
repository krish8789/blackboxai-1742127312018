package com.mealmate.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import com.google.android.material.textfield.TextInputLayout
import com.mealmate.data.MealMateDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.util.Patterns
import com.mealmate.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var database: MealMateDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = MealMateDatabase.getDatabase(this)

        setupClickListeners()
        setupTextChangeListeners()
    }

    private fun setupClickListeners() {
        binding.loginButton.setOnClickListener {
            if (validateInputs()) {
                performLogin()
            }
        }

        binding.registerTextView.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun setupTextChangeListeners() {
        binding.emailEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validateEmail()
            }
        }

        binding.passwordEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validatePassword()
            }
        }
    }

    private fun validateInputs(): Boolean {
        return validateEmail() && validatePassword()
    }

    private fun validateEmail(): Boolean {
        val email = binding.emailEditText.text.toString().trim()
        return when {
            email.isEmpty() -> {
                binding.emailLayout.error = getString(R.string.error_empty_fields)
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding.emailLayout.error = getString(R.string.error_invalid_email)
                false
            }
            else -> {
                binding.emailLayout.error = null
                true
            }
        }
    }

    private fun validatePassword(): Boolean {
        val password = binding.passwordEditText.text.toString()
        return when {
            password.isEmpty() -> {
                binding.passwordLayout.error = getString(R.string.error_empty_fields)
                false
            }
            password.length < 6 -> {
                binding.passwordLayout.error = getString(R.string.error_invalid_password)
                false
            }
            else -> {
                binding.passwordLayout.error = null
                true
            }
        }
    }

    private fun performLogin() {
        val email = binding.emailEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString()

        showLoading(true)

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val user = database.userDao().login(email, password)

                withContext(Dispatchers.Main) {
                    if (user != null) {
                        // Save user session
                        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this@LoginActivity)
                        sharedPreferences.edit().putLong("USER_ID", user.id).apply()

                        // Navigate to HomeActivity
                        startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "Invalid email or password",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    showLoading(false)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@LoginActivity,
                        "Error: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    showLoading(false)
                }
            }
        }
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.loginButton.isEnabled = !show
        binding.emailEditText.isEnabled = !show
        binding.passwordEditText.isEnabled = !show
        binding.registerTextView.isEnabled = !show
    }
}

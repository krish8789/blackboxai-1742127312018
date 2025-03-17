package com.mealmate.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import com.mealmate.data.MealMateDatabase
import com.mealmate.data.model.User
import com.mealmate.databinding.ActivityRegisterBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.util.Patterns

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var database: MealMateDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = MealMateDatabase.getDatabase(this)

        setupClickListeners()
        setupTextChangeListeners()
    }

    private fun setupClickListeners() {
        binding.registerButton.setOnClickListener {
            if (validateInputs()) {
                performRegistration()
            }
        }

        binding.loginTextView.setOnClickListener {
            finish() // Return to LoginActivity
        }
    }

    private fun setupTextChangeListeners() {
        binding.nameEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validateName()
            }
        }

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

        binding.confirmPasswordEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validateConfirmPassword()
            }
        }
    }

    private fun validateInputs(): Boolean {
        return validateName() && validateEmail() && validatePassword() && validateConfirmPassword()
    }

    private fun validateName(): Boolean {
        val name = binding.nameEditText.text.toString().trim()
        return when {
            name.isEmpty() -> {
                binding.nameLayout.error = getString(R.string.error_empty_fields)
                false
            }
            else -> {
                binding.nameLayout.error = null
                true
            }
        }
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

    private fun validateConfirmPassword(): Boolean {
        val password = binding.passwordEditText.text.toString()
        val confirmPassword = binding.confirmPasswordEditText.text.toString()
        return when {
            confirmPassword.isEmpty() -> {
                binding.confirmPasswordLayout.error = getString(R.string.error_empty_fields)
                false
            }
            confirmPassword != password -> {
                binding.confirmPasswordLayout.error = getString(R.string.error_passwords_not_match)
                false
            }
            else -> {
                binding.confirmPasswordLayout.error = null
                true
            }
        }
    }

    private fun performRegistration() {
        val name = binding.nameEditText.text.toString().trim()
        val email = binding.emailEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString()

        showLoading(true)

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // Check if email already exists
                val existingUser = database.userDao().getUserByEmail(email)
                if (existingUser != null) {
                    withContext(Dispatchers.Main) {
                        binding.emailLayout.error = "Email already registered"
                        showLoading(false)
                    }
                    return@launch
                }

                // Create new user
                val user = User(
                    name = name,
                    email = email,
                    password = password // In a real app, this should be hashed
                )

                val userId = database.userDao().insertUser(user)

                withContext(Dispatchers.Main) {
                    // Save user session
                    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this@RegisterActivity)
                    sharedPreferences.edit().putLong("USER_ID", userId).apply()

                    // Navigate to HomeActivity
                    startActivity(Intent(this@RegisterActivity, HomeActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
                    finish()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@RegisterActivity,
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
        binding.registerButton.isEnabled = !show
        binding.nameEditText.isEnabled = !show
        binding.emailEditText.isEnabled = !show
        binding.passwordEditText.isEnabled = !show
        binding.confirmPasswordEditText.isEnabled = !show
        binding.loginTextView.isEnabled = !show
    }
}

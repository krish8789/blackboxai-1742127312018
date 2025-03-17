package com.mealmate.app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.mealmate.data.MealMateDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

class MainActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize SharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        
        // Check if user is logged in
        checkAuthenticationStatus()
    }

    private fun checkAuthenticationStatus() {
        val userId = sharedPreferences.getLong("USER_ID", -1)
        
        if (userId != -1L) {
            // User is logged in, verify in database
            lifecycleScope.launch(Dispatchers.IO) {
                val database = MealMateDatabase.getDatabase(this@MainActivity)
                val user = database.userDao().getUserById(userId)
                
                launch(Dispatchers.Main) {
                    user.collect { userData ->
                        if (userData != null) {
                            // Valid user, go to HomeActivity
                            startActivity(Intent(this@MainActivity, HomeActivity::class.java))
                        } else {
                            // Invalid user, clear preferences and go to LoginActivity
                            sharedPreferences.edit().remove("USER_ID").apply()
                            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                        }
                        finish()
                    }
                }
            }
        } else {
            // No logged in user, go to LoginActivity
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            finish()
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}

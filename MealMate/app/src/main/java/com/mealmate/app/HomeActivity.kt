package com.mealmate.app

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.mealmate.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment)
        
        // Set up the bottom navigation with the nav controller
        binding.bottomNavigation.setupWithNavController(navController)

        // Configure the ActionBar with the nav controller
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_meals,
                R.id.navigation_shopping,
                R.id.navigation_profile
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Set up FAB click listener
        binding.fabAddMeal.setOnClickListener {
            when (navController.currentDestination?.id) {
                R.id.navigation_meals -> {
                    navController.navigate(R.id.action_meals_to_create_meal)
                }
                R.id.navigation_shopping -> {
                    navController.navigate(R.id.action_shopping_to_delegate)
                }
            }
        }

        // Update FAB visibility based on current destination
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.fabAddMeal.show()
            when (destination.id) {
                R.id.navigation_meals -> {
                    binding.fabAddMeal.setImageResource(R.drawable.ic_add)
                }
                R.id.navigation_shopping -> {
                    binding.fabAddMeal.setImageResource(R.drawable.ic_shopping_cart)
                }
                else -> binding.fabAddMeal.hide()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun logout() {
        // Clear user session
        PreferenceManager.getDefaultSharedPreferences(this)
            .edit()
            .remove("USER_ID")
            .apply()

        // Navigate to login screen
        startActivity(Intent(this, LoginActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
        finish()
    }
}

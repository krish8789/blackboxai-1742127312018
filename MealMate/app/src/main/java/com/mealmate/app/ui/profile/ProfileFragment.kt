package com.mealmate.app.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import com.mealmate.app.LoginActivity
import com.mealmate.data.MealMateDatabase
import com.mealmate.data.model.User
import com.mealmate.databinding.FragmentProfileBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var database: MealMateDatabase
    private var currentUser: User? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = MealMateDatabase.getDatabase(requireContext())
        setupClickListeners()
        loadUserProfile()
        loadStatistics()
    }

    private fun setupClickListeners() {
        binding.updateProfileButton.setOnClickListener {
            updateProfile()
        }

        binding.logoutButton.setOnClickListener {
            logout()
        }
    }

    private fun loadUserProfile() {
        val userId = PreferenceManager.getDefaultSharedPreferences(requireContext())
            .getLong("USER_ID", -1)

        if (userId == -1L) {
            logout()
            return
        }

        showLoading(true)
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                database.userDao().getUserById(userId).collectLatest { user ->
                    currentUser = user
                    binding.apply {
                        nameEditText.setText(user.name)
                        emailEditText.setText(user.email)
                    }
                    showLoading(false)
                }
            } catch (e: Exception) {
                showError("Error loading profile: ${e.message}")
                showLoading(false)
            }
        }
    }

    private fun loadStatistics() {
        val userId = PreferenceManager.getDefaultSharedPreferences(requireContext())
            .getLong("USER_ID", -1)

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // Get all recipes for user
                database.recipeDao().getAllRecipesForUser(userId).collectLatest { recipes ->
                    val totalRecipes = recipes.size
                    val totalIngredients = recipes.sumOf { it.ingredients.size }
                    val completedShopping = recipes.sumOf { recipe ->
                        recipe.ingredients.count { it.isPurchased }
                    }

                    binding.apply {
                        totalRecipesText.text = "Total Recipes: $totalRecipes"
                        totalIngredientsText.text = "Total Ingredients: $totalIngredients"
                        completedShoppingText.text = "Items Purchased: $completedShopping"
                    }
                }
            } catch (e: Exception) {
                showError("Error loading statistics: ${e.message}")
            }
        }
    }

    private fun updateProfile() {
        val name = binding.nameEditText.text.toString().trim()
        
        if (name.isBlank()) {
            binding.nameLayout.error = "Name cannot be empty"
            return
        }

        currentUser?.let { user ->
            showLoading(true)
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    val updatedUser = user.copy(name = name)
                    database.userDao().updateUser(updatedUser)
                    
                    showLoading(false)
                    Snackbar.make(binding.root, "Profile updated successfully", Snackbar.LENGTH_SHORT)
                        .show()
                } catch (e: Exception) {
                    showError("Error updating profile: ${e.message}")
                    showLoading(false)
                }
            }
        }
    }

    private fun logout() {
        // Clear user session
        PreferenceManager.getDefaultSharedPreferences(requireContext())
            .edit()
            .remove("USER_ID")
            .apply()

        // Navigate to login screen
        startActivity(
            Intent(requireContext(), LoginActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        )
        requireActivity().finish()
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.updateProfileButton.isEnabled = !show
        binding.logoutButton.isEnabled = !show
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

package com.mealmate.app.ui.meals

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.mealmate.data.MealMateDatabase
import com.mealmate.databinding.FragmentMealsBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MealsFragment : Fragment() {
    private var _binding: FragmentMealsBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var adapter: RecipeAdapter
    private lateinit var database: MealMateDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMealsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupSwipeRefresh()
        loadRecipes()
    }

    private fun setupRecyclerView() {
        adapter = RecipeAdapter(
            onRecipeClick = { recipe ->
                // Navigate to recipe details
                // TODO: Implement navigation to recipe details
            },
            onAddToShoppingList = { recipe ->
                // Add ingredients to shopping list
                // TODO: Implement adding to shopping list
            }
        )

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@MealsFragment.adapter
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            loadRecipes()
        }
    }

    private fun loadRecipes() {
        showLoading(true)
        
        val userId = PreferenceManager.getDefaultSharedPreferences(requireContext())
            .getLong("USER_ID", -1)
        
        if (userId == -1L) {
            showError("User not found")
            return
        }

        database = MealMateDatabase.getDatabase(requireContext())
        
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                database.recipeDao().getAllRecipesForUser(userId).collectLatest { recipes ->
                    adapter.submitList(recipes)
                    binding.emptyView.visibility = if (recipes.isEmpty()) View.VISIBLE else View.GONE
                    showLoading(false)
                }
            } catch (e: Exception) {
                showError("Error loading recipes: ${e.message}")
                showLoading(false)
            }
        }
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.swipeRefresh.isRefreshing = false
    }

    private fun showError(message: String) {
        // TODO: Implement error showing (e.g., using Snackbar)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

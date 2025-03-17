package com.mealmate.app.ui.shopping

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.mealmate.data.MealMateDatabase
import com.mealmate.data.model.Ingredient
import com.mealmate.databinding.FragmentShoppingListBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ShoppingListFragment : Fragment() {
    private var _binding: FragmentShoppingListBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ShoppingListAdapter
    private lateinit var database: MealMateDatabase
    private var shoppingItems = listOf<ShoppingItem>()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            delegateShopping()
        } else {
            Snackbar.make(
                binding.root,
                "SMS permission is required to delegate shopping",
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShoppingListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = MealMateDatabase.getDatabase(requireContext())
        setupRecyclerView()
        setupClickListeners()
        loadShoppingList()
    }

    private fun setupRecyclerView() {
        adapter = ShoppingListAdapter { ingredient, isChecked ->
            updateIngredientStatus(ingredient, isChecked)
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@ShoppingListFragment.adapter
        }
    }

    private fun setupClickListeners() {
        binding.swipeRefresh.setOnRefreshListener {
            loadShoppingList()
        }

        binding.delegateFab.setOnClickListener {
            checkSmsPermissionAndDelegate()
        }
    }

    private fun loadShoppingList() {
        showLoading(true)

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // Get recipes with unpurchased ingredients
                database.recipeDao().getRecipesWithUnpurchasedIngredients().collectLatest { recipes ->
                    // Create shopping items list
                    shoppingItems = recipes.flatMap { recipe ->
                        recipe.ingredients.map { ingredient ->
                            ShoppingItem(ingredient, recipe)
                        }
                    }

                    // Update UI
                    adapter.submitList(shoppingItems)
                    updateSummary()
                    binding.emptyView.visibility = if (shoppingItems.isEmpty()) View.VISIBLE else View.GONE
                    showLoading(false)
                }
            } catch (e: Exception) {
                showError("Error loading shopping list: ${e.message}")
                showLoading(false)
            }
        }
    }

    private fun updateIngredientStatus(ingredient: Ingredient, isPurchased: Boolean) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                database.recipeDao().updateIngredientPurchaseStatus(ingredient.id, isPurchased)
                updateSummary()
            } catch (e: Exception) {
                showError("Error updating ingredient: ${e.message}")
            }
        }
    }

    private fun updateSummary() {
        val totalItems = shoppingItems.size
        val purchasedItems = shoppingItems.count { it.ingredient.isPurchased }

        binding.totalItemsText.text = "$totalItems items in your list"
        binding.purchasedItemsText.text = "$purchasedItems items purchased"
    }

    private fun checkSmsPermissionAndDelegate() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.SEND_SMS
            ) == PackageManager.PERMISSION_GRANTED -> {
                delegateShopping()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.SEND_SMS) -> {
                Snackbar.make(
                    binding.root,
                    "SMS permission is needed to delegate shopping",
                    Snackbar.LENGTH_INDEFINITE
                ).setAction("Grant") {
                    requestPermissionLauncher.launch(Manifest.permission.SEND_SMS)
                }.show()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.SEND_SMS)
            }
        }
    }

    private fun delegateShopping() {
        // Create shopping list text
        val shoppingListText = buildString {
            appendLine("Shopping List:")
            appendLine()
            
            shoppingItems.groupBy { it.recipe.name }.forEach { (recipeName, items) ->
                appendLine("For $recipeName:")
                items.forEach { item ->
                    if (!item.ingredient.isPurchased) {
                        appendLine("- ${item.ingredient.name}: ${item.ingredient.quantity} ${item.ingredient.unit}")
                    }
                }
                appendLine()
            }
        }

        // Open SMS app with pre-filled message
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("smsto:")
            putExtra("sms_body", shoppingListText)
        }

        try {
            startActivity(intent)
        } catch (e: Exception) {
            showError("Error opening SMS app: ${e.message}")
        }
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.swipeRefresh.isRefreshing = false
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

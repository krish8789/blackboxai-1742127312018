package com.mealmate.app.ui.meals

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.mealmate.data.MealMateDatabase
import com.mealmate.data.model.Recipe
import com.mealmate.databinding.FragmentCreateMealBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class CreateMealFragment : Fragment() {
    private var _binding: FragmentCreateMealBinding? = null
    private val binding get() = _binding!!

    private lateinit var ingredientAdapter: IngredientInputAdapter
    private lateinit var database: MealMateDatabase
    private var selectedImageUri: Uri? = null

    private val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedImageUri = uri
                binding.recipeImage.setImageURI(uri)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateMealBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        database = MealMateDatabase.getDatabase(requireContext())
        setupIngredientsList()
        setupClickListeners()
    }

    private fun setupIngredientsList() {
        ingredientAdapter = IngredientInputAdapter(
            onIngredientChanged = { position, ingredient ->
                // Update ingredient in list
                val currentList = ingredientAdapter.currentList.toMutableList()
                currentList[position] = ingredient
                ingredientAdapter.submitList(currentList)
            },
            onRemoveIngredient = { position ->
                val currentList = ingredientAdapter.currentList.toMutableList()
                currentList.removeAt(position)
                ingredientAdapter.submitList(currentList)
            }
        )

        binding.ingredientsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = ingredientAdapter
        }

        // Add initial empty ingredient
        ingredientAdapter.submitList(listOf(IngredientInput()))
    }

    private fun setupClickListeners() {
        binding.imageCard.setOnClickListener {
            openImagePicker()
        }

        binding.addIngredientButton.setOnClickListener {
            val currentList = ingredientAdapter.currentList.toMutableList()
            currentList.add(IngredientInput())
            ingredientAdapter.submitList(currentList)
        }

        binding.saveFab.setOnClickListener {
            saveRecipe()
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        getContent.launch(intent)
    }

    private fun saveRecipe() {
        val name = binding.recipeNameEditText.text.toString()
        val instructions = binding.instructionsEditText.text.toString()
        val ingredients = ingredientAdapter.currentList

        // Validate inputs
        if (name.isBlank()) {
            binding.recipeNameLayout.error = "Recipe name is required"
            return
        }
        if (instructions.isBlank()) {
            binding.instructionsLayout.error = "Instructions are required"
            return
        }
        if (ingredients.isEmpty() || !ingredients.all { it.isValid() }) {
            Snackbar.make(binding.root, "Please fill all ingredient details", Snackbar.LENGTH_SHORT).show()
            return
        }

        showLoading(true)

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val userId = PreferenceManager.getDefaultSharedPreferences(requireContext())
                    .getLong("USER_ID", -1)

                if (userId == -1L) {
                    throw Exception("User not found")
                }

                // Save image if selected
                val imageUrl = selectedImageUri?.let { uri ->
                    saveImageToInternalStorage(uri)
                }

                // Create recipe
                val recipe = Recipe(
                    name = name,
                    instructions = instructions,
                    imageUrl = imageUrl,
                    userId = userId
                )

                // Save recipe and ingredients
                database.recipeDao().insertRecipeWithIngredients(
                    recipe,
                    ingredients.map { it.toIngredient(recipe.id) }
                )

                withContext(Dispatchers.Main) {
                    showLoading(false)
                    findNavController().navigateUp()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    Snackbar.make(binding.root, "Error: ${e.message}", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    private suspend fun saveImageToInternalStorage(uri: Uri): String = withContext(Dispatchers.IO) {
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val file = File(requireContext().filesDir, "recipe_images/${System.currentTimeMillis()}.jpg")
        file.parentFile?.mkdirs()
        
        FileOutputStream(file).use { outputStream ->
            inputStream?.copyTo(outputStream)
        }
        
        file.absolutePath
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.saveFab.isEnabled = !show
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

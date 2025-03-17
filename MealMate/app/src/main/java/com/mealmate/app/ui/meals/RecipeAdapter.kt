package com.mealmate.app.ui.meals

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mealmate.R
import com.mealmate.data.model.Recipe
import com.mealmate.databinding.ItemRecipeBinding

class RecipeAdapter(
    private val onRecipeClick: (Recipe) -> Unit,
    private val onAddToShoppingList: (Recipe) -> Unit
) : ListAdapter<Recipe, RecipeAdapter.RecipeViewHolder>(RecipeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val binding = ItemRecipeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RecipeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class RecipeViewHolder(
        private val binding: ItemRecipeBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onRecipeClick(getItem(position))
                }
            }

            // Find the "Add to Shopping List" chip and set its click listener
            binding.ingredientChips.getChildAt(1).setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onAddToShoppingList(getItem(position))
                }
            }
        }

        fun bind(recipe: Recipe) {
            binding.apply {
                recipeName.text = recipe.name
                ingredientCount.text = "${recipe.ingredients.size} ingredients"

                // Load recipe image using Glide
                if (!recipe.imageUrl.isNullOrEmpty()) {
                    Glide.with(recipeImage)
                        .load(recipe.imageUrl)
                        .placeholder(R.drawable.placeholder_meal)
                        .error(R.drawable.placeholder_meal)
                        .centerCrop()
                        .into(recipeImage)
                } else {
                    // Load placeholder image
                    Glide.with(recipeImage)
                        .load(R.drawable.placeholder_meal)
                        .centerCrop()
                        .into(recipeImage)
                }
            }
        }
    }

    private class RecipeDiffCallback : DiffUtil.ItemCallback<Recipe>() {
        override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
            return oldItem == newItem
        }
    }
}

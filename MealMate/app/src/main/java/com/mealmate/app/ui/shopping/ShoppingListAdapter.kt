package com.mealmate.app.ui.shopping

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mealmate.data.model.Ingredient
import com.mealmate.data.model.Recipe
import com.mealmate.databinding.ItemShoppingBinding

class ShoppingListAdapter(
    private val onItemChecked: (Ingredient, Boolean) -> Unit
) : ListAdapter<ShoppingItem, ShoppingListAdapter.ShoppingViewHolder>(ShoppingItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingViewHolder {
        val binding = ItemShoppingBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ShoppingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ShoppingViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ShoppingViewHolder(
        private val binding: ItemShoppingBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.checkBox.setOnCheckedChangeListener { _, isChecked ->
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    onItemChecked(item.ingredient, isChecked)
                }
            }
        }

        fun bind(item: ShoppingItem) {
            binding.apply {
                checkBox.isChecked = item.ingredient.isPurchased
                ingredientName.text = item.ingredient.name
                recipeName.text = "For: ${item.recipe.name}"
                quantity.text = item.ingredient.quantity
                unit.text = item.ingredient.unit

                // Strike through text if item is purchased
                if (item.ingredient.isPurchased) {
                    ingredientName.paintFlags = ingredientName.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    recipeName.paintFlags = recipeName.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    quantity.paintFlags = quantity.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    unit.paintFlags = unit.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                } else {
                    ingredientName.paintFlags = ingredientName.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                    recipeName.paintFlags = recipeName.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                    quantity.paintFlags = quantity.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                    unit.paintFlags = unit.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                }
            }
        }
    }

    private class ShoppingItemDiffCallback : DiffUtil.ItemCallback<ShoppingItem>() {
        override fun areItemsTheSame(oldItem: ShoppingItem, newItem: ShoppingItem): Boolean {
            return oldItem.ingredient.id == newItem.ingredient.id
        }

        override fun areContentsTheSame(oldItem: ShoppingItem, newItem: ShoppingItem): Boolean {
            return oldItem == newItem
        }
    }
}

data class ShoppingItem(
    val ingredient: Ingredient,
    val recipe: Recipe
)

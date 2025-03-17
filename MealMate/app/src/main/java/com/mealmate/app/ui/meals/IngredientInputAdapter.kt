package com.mealmate.app.ui.meals

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mealmate.data.model.Ingredient
import com.mealmate.data.model.Unit
import com.mealmate.databinding.ItemIngredientInputBinding

class IngredientInputAdapter(
    private val onIngredientChanged: (Int, Ingredient) -> Unit,
    private val onRemoveIngredient: (Int) -> Unit
) : ListAdapter<IngredientInput, IngredientInputAdapter.IngredientViewHolder>(IngredientDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val binding = ItemIngredientInputBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return IngredientViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class IngredientViewHolder(
        private val binding: ItemIngredientInputBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            // Set up unit dropdown
            val units = Unit.values().map { it.name }
            val unitAdapter = ArrayAdapter(
                binding.root.context,
                android.R.layout.simple_dropdown_item_1line,
                units
            )
            binding.unitAutoComplete.setAdapter(unitAdapter)

            // Set up text change listeners
            binding.ingredientNameEditText.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) updateIngredient()
            }
            binding.quantityEditText.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) updateIngredient()
            }
            binding.unitAutoComplete.setOnItemClickListener { _, _, _, _ ->
                updateIngredient()
            }

            // Set up remove button
            binding.removeButton.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onRemoveIngredient(position)
                }
            }
        }

        fun bind(ingredientInput: IngredientInput) {
            binding.apply {
                ingredientNameEditText.setText(ingredientInput.name)
                quantityEditText.setText(ingredientInput.quantity)
                unitAutoComplete.setText(ingredientInput.unit, false)
            }
        }

        private fun updateIngredient() {
            val position = bindingAdapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val name = binding.ingredientNameEditText.text.toString()
                val quantity = binding.quantityEditText.text.toString()
                val unit = binding.unitAutoComplete.text.toString()

                onIngredientChanged(
                    position,
                    IngredientInput(name, quantity, unit)
                )
            }
        }
    }

    private class IngredientDiffCallback : DiffUtil.ItemCallback<IngredientInput>() {
        override fun areItemsTheSame(oldItem: IngredientInput, newItem: IngredientInput): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: IngredientInput, newItem: IngredientInput): Boolean {
            return oldItem == newItem
        }
    }
}

data class IngredientInput(
    val name: String = "",
    val quantity: String = "",
    val unit: String = Unit.PIECES.name
) {
    fun isValid(): Boolean {
        return name.isNotBlank() && quantity.isNotBlank() && unit.isNotBlank()
    }

    fun toIngredient(recipeId: Long): Ingredient {
        return Ingredient(
            recipeId = recipeId,
            name = name,
            quantity = quantity,
            unit = unit
        )
    }
}

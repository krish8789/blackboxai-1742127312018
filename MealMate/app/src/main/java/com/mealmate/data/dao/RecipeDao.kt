package com.mealmate.data.dao

import androidx.room.*
import com.mealmate.data.model.Recipe
import com.mealmate.data.model.Ingredient
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {
    @Transaction
    @Query("SELECT * FROM recipes WHERE userId = :userId")
    fun getAllRecipesForUser(userId: Long): Flow<List<Recipe>>

    @Transaction
    @Query("SELECT * FROM recipes WHERE id = :recipeId")
    fun getRecipeById(recipeId: Long): Flow<Recipe>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: Recipe): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIngredient(ingredient: Ingredient): Long

    @Transaction
    suspend fun insertRecipeWithIngredients(recipe: Recipe, ingredients: List<Ingredient>) {
        val recipeId = insertRecipe(recipe)
        ingredients.forEach { ingredient ->
            insertIngredient(ingredient.copy(recipeId = recipeId))
        }
    }

    @Query("SELECT * FROM ingredients WHERE recipeId = :recipeId")
    fun getIngredientsForRecipe(recipeId: Long): Flow<List<Ingredient>>

    @Query("SELECT * FROM ingredients WHERE isPurchased = 0")
    fun getUnpurchasedIngredients(): Flow<List<Ingredient>>

    @Update
    suspend fun updateIngredient(ingredient: Ingredient)

    @Delete
    suspend fun deleteRecipe(recipe: Recipe)

    @Delete
    suspend fun deleteIngredient(ingredient: Ingredient)

    @Query("UPDATE ingredients SET isPurchased = :isPurchased WHERE id = :ingredientId")
    suspend fun updateIngredientPurchaseStatus(ingredientId: Long, isPurchased: Boolean)

    @Transaction
    @Query("SELECT DISTINCT r.* FROM recipes r INNER JOIN ingredients i ON r.id = i.recipeId WHERE i.isPurchased = 0")
    fun getRecipesWithUnpurchasedIngredients(): Flow<List<Recipe>>
}

package com.mealmate.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mealmate.data.dao.UserDao
import com.mealmate.data.dao.RecipeDao
import com.mealmate.data.model.User
import com.mealmate.data.model.Recipe
import com.mealmate.data.model.Ingredient

@Database(
    entities = [User::class, Recipe::class, Ingredient::class],
    version = 1,
    exportSchema = false
)
abstract class MealMateDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun recipeDao(): RecipeDao

    companion object {
        @Volatile
        private var INSTANCE: MealMateDatabase? = null

        fun getDatabase(context: Context): MealMateDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MealMateDatabase::class.java,
                    "mealmate_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

// Repository class for handling data operations
class MealMateRepository(private val database: MealMateDatabase) {
    // User operations
    private val userDao = database.userDao()
    
    suspend fun registerUser(user: User) = userDao.insertUser(user)
    suspend fun loginUser(email: String, password: String) = userDao.login(email, password)
    fun getUserById(userId: Long) = userDao.getUserById(userId)
    
    // Recipe operations
    private val recipeDao = database.recipeDao()
    
    fun getAllRecipesForUser(userId: Long) = recipeDao.getAllRecipesForUser(userId)
    fun getRecipeById(recipeId: Long) = recipeDao.getRecipeById(recipeId)
    suspend fun createRecipe(recipe: Recipe, ingredients: List<Ingredient>) = 
        recipeDao.insertRecipeWithIngredients(recipe, ingredients)
    
    fun getUnpurchasedIngredients() = recipeDao.getUnpurchasedIngredients()
    suspend fun updateIngredientPurchaseStatus(ingredientId: Long, isPurchased: Boolean) = 
        recipeDao.updateIngredientPurchaseStatus(ingredientId, isPurchased)
    
    fun getRecipesWithUnpurchasedIngredients() = recipeDao.getRecipesWithUnpurchasedIngredients()
}

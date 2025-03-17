package com.mealmate.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val instructions: String,
    val imageUrl: String?,
    val userId: Long, // Foreign key to User
    val createdAt: Long = System.currentTimeMillis()
) {
    @Relation(
        parentColumn = "id",
        entityColumn = "recipeId"
    )
    var ingredients: List<Ingredient> = emptyList()
}

@Entity(tableName = "ingredients")
data class Ingredient(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val recipeId: Long, // Foreign key to Recipe
    val name: String,
    val quantity: String,
    val unit: String,
    var isPurchased: Boolean = false
)

enum class Unit {
    GRAMS,
    KILOGRAMS,
    MILLILITERS,
    LITERS,
    PIECES,
    TABLESPOONS,
    TEASPOONS,
    CUPS,
    NONE
}

package com.example.foodmanager

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RecipeListActivity : AppCompatActivity() {
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var recipeAdapter: RecipeAdapter
    private lateinit var recipes: List<Recipe>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipes_list)

        databaseHelper = DatabaseHelper(this)
        recyclerView = findViewById(R.id.recyclerView)

        recipes = databaseHelper.getAllRecipes()

        recipeAdapter = RecipeAdapter(recipes)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = recipeAdapter

        val buttonCreateRecipe: Button = findViewById(R.id.CreateRecipeButton)
        buttonCreateRecipe.setOnClickListener {
            val intent = Intent(this, CreateRecipeActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getRecipesFromDatabase(): List<Recipe> {
        val recipeList = mutableListOf<Recipe>()
        val db = databaseHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM ${DatabaseHelper.TABLE_RECIPE}", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID))
                val titleIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_TITLE)
                val servings = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_SERVINGS))
                val stepsIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_STEPS)
                val imageIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_IMAGE)

                val title = if (titleIndex != -1) cursor.getString(titleIndex) else ""
                val steps = if (stepsIndex != -1) cursor.getString(stepsIndex) else ""
                val image = if (imageIndex != -1) cursor.getBlob(imageIndex) else null

                // Получаем ингредиенты для данного рецепта
                val ingredients = getIngredientsForRecipe(id)

                val recipe = Recipe(id, title, ingredients, servings, steps, image) // Заголовок передается
                recipeList.add(recipe)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return recipeList
    }

    private fun getIngredientsForRecipe(recipeId: Int): List<ProductQuantity> {
        val ingredientsList = mutableListOf<ProductQuantity>()
        val db = databaseHelper.readableDatabase
        val cursor = db.rawQuery("SELECT product_name, quantity FROM ${DatabaseHelper.TABLE_RECIPE_INGREDIENTS} WHERE recipe_id = ?",
            arrayOf(recipeId.toString()))

        if (cursor.moveToFirst()) {
            do {
                val productName = cursor.getString(cursor.getColumnIndex("product_name"))
                val quantity = cursor.getInt(cursor.getColumnIndex("quantity"))
                ingredientsList.add(ProductQuantity(productName, quantity))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return ingredientsList
    }
}
package com.example.foodmanager

import android.content.Intent
import android.database.Cursor
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
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

        // Загрузите рецепты из базы данных
        recipes = getRecipesFromDatabase()

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
        var cursor: Cursor? = null

        try {
            // Получаем все записи из таблицы рецептов
            cursor = db.rawQuery("SELECT * FROM ${DatabaseHelper.TABLE_RECIPE}", null)

            if (cursor.moveToFirst()) {
                do {
                    val idIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)
                    val titleIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_TITLE)
                    val servingsIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_SERVINGS)
                    val stepsIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_STEPS)
                    val imageIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_IMAGE)

                    if (idIndex != -1 && titleIndex != -1 && servingsIndex != -1 && stepsIndex != -1 && imageIndex != -1) {
                        val id = cursor.getInt(idIndex)
                        val title = cursor.getString(titleIndex) ?: ""
                        val servings = cursor.getInt(servingsIndex)
                        val steps = cursor.getString(stepsIndex) ?: ""
                        val imagePath = cursor.getString(imageIndex) ?: "" // Обработка возможного null

                        // Получаем ингредиенты для данного рецепта
                        val ingredients = getIngredientsForRecipe(id)

                        recipeList.add(Recipe(id, title, ingredients, servings, steps, imagePath))
                    } else {
                        Log.e("RecipeListActivity", "One or more columns are missing in the cursor")
                    }
                } while (cursor.moveToNext())
            } else {
                Log.e("RecipeListActivity", "No recipes found in the database.")
            }
        } catch (e: Exception) {
            Log.e("RecipeListActivity", "Error retrieving recipes: ${e.message}")
        } finally {
            cursor?.close()
            db.close()
        }
        return recipeList
    }

    private fun getIngredientsForRecipe(recipeId: Int): List<ProductQuantity> {
        val ingredientsList = mutableListOf<ProductQuantity>()
        val db = databaseHelper.readableDatabase
        var cursor: Cursor? = null

        try {
            cursor = db.rawQuery(
                "SELECT ${DatabaseHelper.COLUMN_PRODUCT_NAME}, ${DatabaseHelper.COLUMN_QUANTITY} FROM ${DatabaseHelper.TABLE_RECIPE_INGREDIENTS} WHERE ${DatabaseHelper.COLUMN_RECIPE_ID} = ?",
                arrayOf(recipeId.toString())
            )

            if (cursor.moveToFirst()) {
                do {
                    val productName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_NAME)) ?: ""
                    val quantity = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_QUANTITY))
                    ingredientsList.add(ProductQuantity(productName, quantity))
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            Log.e("RecipeListActivity", "Error while retrieving ingredients: ${e.message}")
        } finally {
            cursor?.close()
        }

        return ingredientsList
    }

    override fun onDestroy() {
        databaseHelper.close()
        super.onDestroy()
    }
}
package com.example.foodmanager

import android.content.Intent
import android.database.Cursor
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
        recipes = getRecipesFromDatabase() // Замените на этот вызов

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
                val idIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)
                val titleIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_TITLE)
                val servingsIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_SERVINGS)
                val stepsIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_STEPS)
                val imageIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_IMAGE)

                if (idIndex != -1 && titleIndex != -1 && servingsIndex != -1 && stepsIndex != -1) {
                    val id = cursor.getInt(idIndex)
                    val title = cursor.getString(titleIndex) ?: ""
                    val servings = cursor.getInt(servingsIndex)
                    val steps = cursor.getString(stepsIndex) ?: ""
                    val image = cursor.getBlob(imageIndex)

                    // Получаем ингредиенты для данного рецепта
                    val ingredients = getIngredientsForRecipe(id)

                    recipeList.add(Recipe(id, title, ingredients, servings, steps, image))
                } else {
                    Log.e("RecipeListActivity", "One or more columns are missing in the cursor")
                }
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return recipeList
    }

    private fun getIngredientsForRecipe(recipeId: Int): List<ProductQuantity> {
        val ingredientsList = mutableListOf<ProductQuantity>()
        val db = databaseHelper.readableDatabase
        var cursor: Cursor? = null

        try {
            // Выполняем запрос к базе данных для получения ингредиентов
            cursor = db.rawQuery(
                "SELECT ${DatabaseHelper.COLUMN_PRODUCT_NAME}, ${DatabaseHelper.COLUMN_QUANTITY} FROM ${DatabaseHelper.TABLE_RECIPE_INGREDIENTS} WHERE ${DatabaseHelper.COLUMN_RECIPE_ID} = ?",
                arrayOf(recipeId.toString())
            )

            // Проверяем, если курсор имеет результаты
            if (cursor.moveToFirst()) {
                do {
                    // Получаем индексы колонок
                    val productNameIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_NAME)
                    val quantityIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_QUANTITY)

                    // Проверяем, что индексы валидны
                    if (productNameIndex != -1 && quantityIndex != -1) {
                        val productName = cursor.getString(productNameIndex) ?: ""
                        val quantity = cursor.getInt(quantityIndex)
                        ingredientsList.add(ProductQuantity(productName, quantity))
                    } else {
                        Log.e("RecipeListActivity", "Column indexes for product name or quantity not found.")
                    }
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            Log.e("RecipeListActivity", "Error while retrieving ingredients: ${e.message}")
        } finally {
            cursor?.close() // Закрываем курсор, если он не null
            db.close() // Закрываем базу данных
        }

        return ingredientsList
    }
}
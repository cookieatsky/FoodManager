package com.example.foodmanager

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class RecipeListActivity : AppCompatActivity() {
    private lateinit var databaseHelper: DatabaseHelper // предположительно ваш класс для работы с базой данных
    private lateinit var recyclerView: RecyclerView
    private lateinit var recipeAdapter: RecipeAdapter // перепишите его по аналогии с ProductAdapter
    private lateinit var recipes: List<Recipe> // создайте класс Recipe для вашей модели рецепта

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipes_list)

        databaseHelper = DatabaseHelper(this)
        recyclerView = findViewById(R.id.recyclerView)

        recipes = getRecipesFromDatabase() // Получение рецептов из БД

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
                val product = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT))
                val servings = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_SERVINGS))
                val steps = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_STEPS))
                val image = cursor.getBlob(cursor.getColumnIndex(DatabaseHelper.COLUMN_IMAGE)) // Если используете изображение

                val recipe = Recipe(id, product, servings, steps, image)
                recipeList.add(recipe)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return recipeList
    }
}
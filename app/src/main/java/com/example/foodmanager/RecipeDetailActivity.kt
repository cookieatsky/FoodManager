package com.example.foodmanager

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class RecipeDetailActivity : AppCompatActivity() {

    private lateinit var titleTextView: TextView
    private lateinit var servingsTextView: TextView
    private lateinit var stepsTextView: TextView
    private lateinit var imageView: ImageView
    private lateinit var deleteButton: Button
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_detail)

        databaseHelper = DatabaseHelper(this)

        titleTextView = findViewById(R.id.titleTextView)
        servingsTextView = findViewById(R.id.servingsTextView)
        stepsTextView = findViewById(R.id.stepsTextView)
        imageView = findViewById(R.id.imageView)
        deleteButton = findViewById(R.id.deleteButton)

        val recipeId = intent.getIntExtra("RECIPE_ID", -1)

        val recipe = databaseHelper.getRecipeById(recipeId)

        if (recipe != null) {
            titleTextView.text = recipe.title
            servingsTextView.text = "Количество порций: ${recipe.servings}"
            stepsTextView.text = recipe.steps

            // Загружаем изображение из файла по пути
            val imagePath: String? = recipe.imagePath // Теперь ожидаем строку с путем к изображению
            if (!imagePath.isNullOrBlank()) { // Используем безопасный вызов
                val bitmap = BitmapFactory.decodeFile(imagePath)
                imageView.setImageBitmap(bitmap)
            }

            deleteButton.setOnClickListener {
                databaseHelper.deleteRecipe(recipe.id) // Удаление рецепта
                finish() // Закрываем активити
            }
        }
    }
}
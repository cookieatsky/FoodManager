package com.example.foodmanager

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
//import kotlinx.android.synthetic.main.activity_create_product.* // Импортируйте это для использования синтетических свойств

class CreateRecipeActivity : AppCompatActivity() {
    private lateinit var spinnerProducts: Spinner
    private lateinit var servingsInput: EditText
    private lateinit var stepsInput: EditText

    //Добавьте переменную для изображения

    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_recipes)

        databaseHelper = DatabaseHelper(this)

        //Инициализация полей ввода
        spinnerProducts = findViewById(R.id.spinner_products)
        servingsInput = findViewById(R.id.edit_text_servings)
        stepsInput = findViewById(R.id.edit_text_steps)

        // Загрузка существующих продуктов в Spinner
        loadProductsIntoSpinner()

        // Обработчик нажатия кнопки "Сохранить"
        val buttonSaveRecipe: Button = findViewById(R.id.button_save_recipe)
        buttonSaveRecipe.setOnClickListener {
            saveRecipe()
        }
    }

    private fun loadProductsIntoSpinner() {
        // Загрузка списка продуктов из базы данных
        val products = databaseHelper.getAllProducts() // Получаем список продуктов
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, products)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) // Задаем стиль выпадающего списка
        spinnerProducts.adapter = adapter
    }

    private fun saveRecipe() {
        val product = spinnerProducts.selectedItem.toString()
        val servings = servingsInput.text.toString().toIntOrNull() ?: 0
        val steps = stepsInput.text.toString()

        if (product.isNotBlank() && servings > 0 && steps.isNotBlank()) {
            // Сохранение рецепта в базе данных
            databaseHelper.addRecipe(product, servings, steps, null) // замените null, если добавите изображение

            Toast.makeText(this, "Рецепт сохранен!", Toast.LENGTH_SHORT).show()
            finish() // Вернуться к RecipeListActivity
        } else {
            Toast.makeText(this, "Заполните все поля перед сохранением", Toast.LENGTH_SHORT).show()
        }
    }
}